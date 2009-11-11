package org.notedroid.gui.activities;

import org.notedroid.R;
import org.notedroid.model.NotesDbAdapter;
import org.notedroid.utils.ApplicationUtils;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

public class NoteEditor extends Activity {
	
	public static final String NOTEEDITOR_MODE = "NOTEEDITOR_MODE";
	public static final String NOTEEDITOR_MODE_SHOW = "NOTEEDITOR_MODE_SHOW";
	public static final String NOTEEDITOR_MODE_EDIT = "NOTEEDITOR_MODE_EDIT";	
	
	private static final int MENU_SAVE = Menu.FIRST;
    private static final int MENU_CANCEL = Menu.FIRST + 1;
	
	private EditText mTitleText;
    private EditText mBodyText;
    private String mNoteMode;
    private Long mRowId;
    private Long mParentId;
    
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.noteeditor);
       
        mTitleText = (EditText) findViewById(R.id.NoteEditor_NoteName);
        mBodyText = (EditText) findViewById(R.id.NoteEditor_Body);              

        Bundle extras = getIntent().getExtras();
    	if (extras != null) {
    		mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
    		mParentId = extras.getLong(NotesDbAdapter.KEY_PARENTID);
    		mNoteMode = extras.getString(NoteEditor.NOTEEDITOR_MODE);
    	} else if (savedInstanceState != null) {
        	mRowId = savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID);
        	mParentId = savedInstanceState.getLong(NotesDbAdapter.KEY_PARENTID);
        	mNoteMode = savedInstanceState.getString(NoteEditor.NOTEEDITOR_MODE);
        } else {
        	mRowId = new Long(-1);
        	mParentId = new Long(-1);
        	mNoteMode = NOTEEDITOR_MODE_SHOW;
        }
        
        if (mNoteMode.equals(NOTEEDITOR_MODE_EDIT)) {
        	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
        	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        
        populateFields();               
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem item;
        item = menu.add(0, MENU_SAVE, 0, R.string.NoteEditor_SaveMenu);
        item.setIcon(R.drawable.save32);
        
        item = menu.add(0, MENU_CANCEL, 0, R.string.NoteEditor_CancelMenu);
        item.setIcon(R.drawable.cancel32);
    	
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) {
        case MENU_SAVE:
        	saveAndExit();
            return true;            
        case MENU_CANCEL:
        	exitWithoutSave();
        	return true;               	
        }    	
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void saveAndExit() {
    	saveData();
    	setResult(RESULT_OK);
        finish();
    }
    
    private void exitWithoutSave() {
    	setResult(RESULT_OK);
        finish();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
        outState.putLong(NotesDbAdapter.KEY_PARENTID, mParentId);
        outState.putString(NoteEditor.NOTEEDITOR_MODE, mNoteMode);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		saveAndExit();
    	} 
    	return super.onKeyDown(keyCode, event);
    }
    
    private void saveData() {
    	String title = mTitleText.getText().toString();
    	String body = mBodyText.getText().toString();

    	if ((title == null) || 
    			(title.length() == 0)) {
    		title = this.getString(R.string.Commons_NewNoteName);
    	}

    	if (mRowId == -1) {
    		long id = mDbHelper.createNote(mParentId, title, body);
    		if (id > 0) {
    			mRowId = id;

    			Bundle extras = getIntent().getExtras();
    			if (extras != null) {
    				extras.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
    				extras.putLong(NotesDbAdapter.KEY_PARENTID, mParentId);
    				extras.putString(NoteEditor.NOTEEDITOR_MODE, mNoteMode);
    			}
    		}
    	} else {
    		mDbHelper.updateNote(mRowId, title, body);
    	}

    	ApplicationUtils.showToasterNotification(this, this.getString(R.string.Commons_NoteSaved));
    }
    
    private void populateFields() {
        if (mRowId != -1) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
    	            note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        } else {
        	mTitleText.setHint(R.string.Commons_NewNoteName);
        }
    }

}
