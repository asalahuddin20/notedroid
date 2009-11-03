package org.notedroid.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.notedroid.preferences.PreferencesConstants;
import org.notedroid.utils.DateUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotesDbAdapter {
	
	public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PARENTID = "parentid";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CREATION_DATE = "creation_date";
    public static final String KEY_MODIFICATION_DATE = "modification_date";
    
    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String DATABASE_NAME = "NOTEDROID";
    private static final String DATABASE_TABLE = "NOTES";
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " (" + 
    			KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    			KEY_PARENTID + " INTEGER DEFAULT -1, " + 
    			KEY_TYPE + " INTEGER, " +
    			KEY_CREATION_DATE + " TEXT, " +
    			KEY_MODIFICATION_DATE + " TEXT, " +
    			KEY_TITLE + " TEXT NOT NULL, " + 
    			KEY_BODY + " TEXT);";    
    
    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
		}
    	
    }
    
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public long createNote(long parentId, String title, String body) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, Note.TYPE_NOTE);
        initialValues.put(KEY_PARENTID, parentId);
        initialValues.put(KEY_CREATION_DATE, DateUtils.getNow(mCtx));
        initialValues.put(KEY_MODIFICATION_DATE, DateUtils.getNow(mCtx));
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public long createFolder(String title, long parentId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, Note.TYPE_FOLDER);
        initialValues.put(KEY_PARENTID, parentId);
        initialValues.put(KEY_CREATION_DATE, DateUtils.getNow(mCtx));
        initialValues.put(KEY_MODIFICATION_DATE, DateUtils.getNow(mCtx));
        initialValues.put(KEY_TITLE, title);        

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public Cursor fetchNotesByParent(long parentId) {
    	String sortMode = PreferenceManager.getDefaultSharedPreferences(mCtx).getString(PreferencesConstants.SORT_MODE, PreferencesConstants.SORT_MODE_NAME);
    	String sortDirection = PreferenceManager.getDefaultSharedPreferences(mCtx).getString(PreferencesConstants.SORT_DIRECTION, PreferencesConstants.SORT_DIRECTION_ASC);
    	
    	String sortDirectionValue;
    	String sortFieldValue;
    	
    	if (sortDirection.equals(PreferencesConstants.SORT_DIRECTION_DESC)) {
    		sortDirectionValue = "DESC";    		
    	} else {
    		sortDirectionValue = "ASC";    		
    	}
    	
    	if (sortMode.equals(PreferencesConstants.SORT_MODE_NAME)) {
    		sortFieldValue = KEY_TITLE;
    	} else if (sortMode.equals(PreferencesConstants.SORT_MODE_MODIFICATION_DATE)) {
    		sortFieldValue = KEY_MODIFICATION_DATE;
    	} else if (sortMode.equals(PreferencesConstants.SORT_MODE_CREATION_DATE)) {
    		sortFieldValue = KEY_CREATION_DATE;
    	} else {
    		sortFieldValue = KEY_TITLE;
    	}    	    	
    	
    	return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE}, KEY_PARENTID + "=" + parentId, null, null, null, sortFieldValue + " " + sortDirectionValue);
    }
    
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_BODY}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;

    }
    
    public int getNoteType(long rowId) {
    	
    	int result = Note.TYPE_NOTE;
    	
    	Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_TYPE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
    	
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    		
    		result = mCursor.getInt(mCursor.getColumnIndex(KEY_TYPE));
    		
    		mCursor.close();
    	}
    	
    	return result;
    }
    
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_MODIFICATION_DATE, DateUtils.getNow(mCtx));

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateTitle(long rowId, String title) {
    	ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_MODIFICATION_DATE, DateUtils.getNow(mCtx));
        
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    private List<Integer> getChildren(long parentId) {
    	List<Integer> result = new ArrayList<Integer>();
    	
    	Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID}, KEY_PARENTID + "=" + parentId, null, null, null, null, null);
    	
    	if (mCursor != null) {
    		while (mCursor.moveToNext()) {
    			result.add(mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID)));
    		}
    		
    		mCursor.close();
    	}
    	
    	return result;
    }
    
    public boolean deleteNote(long rowId) {
    	
    	int noteType = getNoteType(rowId);
    	
    	if (noteType == Note.TYPE_FOLDER) {
    		Iterator<Integer> iter = getChildren(rowId).iterator();
    		while (iter.hasNext()) {
    			deleteNote(iter.next());
    		}
    	}
    	
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public Note getNoteById(long rowId) {
    	Cursor note = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PARENTID, KEY_TYPE, KEY_CREATION_DATE, KEY_MODIFICATION_DATE, KEY_TITLE, KEY_BODY},
    			KEY_ROWID + "=" + rowId, null, null, null, null, null);
    	
    	if (note != null) {
    		note.moveToFirst();
    		
    		Note result = new Note(note.getLong(note.getColumnIndexOrThrow(KEY_ROWID)),
    								note.getLong(note.getColumnIndexOrThrow(KEY_PARENTID)),
    								note.getInt(note.getColumnIndexOrThrow(KEY_TYPE)),
    								DateUtils.convertFromDatabase(mCtx, note.getString(note.getColumnIndexOrThrow(KEY_CREATION_DATE))),
    								DateUtils.convertFromDatabase(mCtx, note.getString(note.getColumnIndexOrThrow(KEY_MODIFICATION_DATE))),
    								note.getString(note.getColumnIndexOrThrow(KEY_TITLE)),
    								note.getString(note.getColumnIndexOrThrow(KEY_BODY)));
    		
    		note.close();
    		
    		return result;
    		
    	} else {
    		return null;
    	}
    }

}
