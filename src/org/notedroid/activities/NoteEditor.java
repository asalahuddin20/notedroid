package org.notedroid.activities;

import org.notedroid.R;
import org.notedroid.model.NotesDbAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEditor extends Activity {
	
	private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private Long mParentId;
    
    private NotesDbAdapter mDbHelper;
    
    private boolean mBoIsCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.noteeditor);
       
        mTitleText = (EditText) findViewById(R.id.NoteEditor_NoteName);
        mBodyText = (EditText) findViewById(R.id.NoteEditor_Body);
      
        Button saveButton = (Button) findViewById(R.id.NoteEditor_ButtonSave);
        Button closeButton = (Button) findViewById(R.id.NoteEditor_ButtonClose);  

        if (savedInstanceState != null) {
        	mRowId = savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID);
        	mParentId = savedInstanceState.getLong(NotesDbAdapter.KEY_PARENTID);
        } else {
        	Bundle extras = getIntent().getExtras();
        	if (extras != null) {
        		mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
        		mParentId = extras.getLong(NotesDbAdapter.KEY_PARENTID);
        	} else {
        		mRowId = new Long(-1);
        		mParentId = new Long(-1);
        	}
        }
        
        populateFields();
       
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	setResult(RESULT_OK);
                finish();
            }
          
        });
        
        closeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	mBoIsCancelled = true;
            	setResult(RESULT_OK);            	
                finish();
            }
          
        });
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private String getDefaultNoteName() {
    	return "NoName";
    }
    
    private void saveState() {
    	if (!mBoIsCancelled) {
    		String title = mTitleText.getText().toString();
    		String body = mBodyText.getText().toString();

    		if ((title == null) || 
    				(title.length() == 0)) {
    			title = getDefaultNoteName();
    		}

    		if (mRowId == -1) {
    			long id = mDbHelper.createNote(mParentId, title, body);
    			if (id > 0) {
    				mRowId = id;
    			}
    		} else {
    			mDbHelper.updateNote(mRowId, title, body);
    		}        
    	}
    }
    
    private void populateFields() {
        if (mRowId != -1) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
    	            note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }

}