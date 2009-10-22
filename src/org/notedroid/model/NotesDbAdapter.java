package org.notedroid.model;

import org.notedroid.preferences.PreferencesConstants;

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

    public static final int TYPE_NOTE = 0;
    public static final int TYPE_CATEGORY = 1;
    
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
        initialValues.put(KEY_TYPE, TYPE_NOTE);
        initialValues.put(KEY_PARENTID, parentId);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public long createCategory(String title, long parentId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, TYPE_CATEGORY);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_PARENTID, parentId);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public Cursor fetchNotesByParent(long parentId) {
    	String sortMode = PreferenceManager.getDefaultSharedPreferences(mCtx).getString(PreferencesConstants.SORT_MODE, PreferencesConstants.SORT_MODE_ASC);
    	
    	if (sortMode.equals(PreferencesConstants.SORT_MODE_DESC)) {
    		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE}, KEY_PARENTID + "=" + parentId, null, null, null, KEY_TITLE + " DESC");
    	} else {
    		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE}, KEY_PARENTID + "=" + parentId, null, null, null, KEY_TITLE + " ASC");
    	}
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
    
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateCategory(long rowId, String title) {
    	ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public int getTypeById(long rowId) {
    	Cursor note = mDb.query(true, DATABASE_TABLE, new String[] {KEY_TYPE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
    	
    	if (note != null) {
    		note.moveToFirst();
    		
    		int result = note.getInt(note.getColumnIndexOrThrow(KEY_TYPE));
    		
    		note.close();
    		
    		return result;
    		
        } else {
        	return TYPE_NOTE;
        }
    }
    
    public int getParentById(long rowId) {
    	Cursor note = mDb.query(true, DATABASE_TABLE, new String[] {KEY_PARENTID}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
    	
    	if (note != null) {
    		note.moveToFirst();
    		
    		int result = note.getInt(note.getColumnIndexOrThrow(KEY_PARENTID));
    		
    		note.close();
    		
    		return result;
    		
    	} else {
    		return -1;
    	}
    }

}
