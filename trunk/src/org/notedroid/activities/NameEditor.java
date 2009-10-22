package org.notedroid.activities;

import org.notedroid.R;
import org.notedroid.model.NotesDbAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NameEditor extends Activity {
	
	private EditText mCategoryNameText;
	private Long mRowId;
	private Long mParentId;
	
	private NotesDbAdapter mDbHelper;
    
    private boolean mBoIsCancelled = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.nameeditor);
        
        mCategoryNameText = (EditText) findViewById(R.id.NameEditor_NameEditText);
        
        Button saveButton = (Button) findViewById(R.id.NameEditor_SaveButton);
        Button cancelButton = (Button) findViewById(R.id.NameEditor_CancelButton);
        
        if (savedInstanceState != null) {
        	mRowId = savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID);
        	mParentId = savedInstanceState.getLong(NotesDbAdapter.KEY_PARENTID);
        } else {
        	Bundle extras = getIntent().getExtras();
        	if (extras != null) {
        		mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
        		mParentId = extras.getLong(NotesDbAdapter.KEY_PARENTID);
        	} else {
        		mRowId = null;
        		mParentId = null;
        	}
        }     
        
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	setResult(RESULT_OK);
                finish();
            }
          
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	mBoIsCancelled = true;
            	setResult(RESULT_OK);            	
                finish();
            }
          
        });
        
        populateFields();
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
    
    private String getDefaultCategoryName() {
    	return "NoName";
    }
    
    private void populateFields() {
        if (mRowId != -1) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            
            mCategoryNameText.setText(note.getString(
    	            note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));            
        }
    }
    
    private void saveState() {
    	if (!mBoIsCancelled) {
    		String title = mCategoryNameText.getText().toString();

    		if ((title == null) || 
    				(title.length() == 0)) {
    			title = getDefaultCategoryName();
    		}

    		if (mRowId == -1) {
    			long id = mDbHelper.createCategory(title, mParentId);
    			if (id > 0) {
    				mRowId = id;
    			}
    		} else {
    			mDbHelper.updateCategory(mRowId, title);
    		}        
    	}
    }
        
}
