package org.notedroid.gui.activities;

import org.notedroid.R;
import org.notedroid.model.Note;
import org.notedroid.model.NotesDbAdapter;
import org.notedroid.utils.DateUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NameEditor extends Activity {
	
	private static final int MENU_SAVE = Menu.FIRST;
    private static final int MENU_CANCEL = Menu.FIRST + 1;
	
    private TextView mTitleText;
	private EditText mNameText;
	private TextView mTypeText;
	private TextView mModificationDateText;
	private TextView mCreationDateText;
	
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
        
        mTitleText = (TextView) findViewById(R.id.NameEditor_TitleText);
        mNameText = (EditText) findViewById(R.id.NameEditor_NameEditText);
        
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
        
        mTypeText = (TextView) findViewById(R.id.NameEditor_TypeText);
        mModificationDateText = (TextView) findViewById(R.id.NameEditor_ModificationDateText);
        mCreationDateText = (TextView) findViewById(R.id.NameEditor_CreationDateText);
        
        populateFields();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem item;
        item = menu.add(0, MENU_SAVE, 0, R.string.NameEditor_SaveMenu);
        item.setIcon(R.drawable.save32);
        
        item = menu.add(0, MENU_CANCEL, 0, R.string.NameEditor_CancelMenu);
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
    	setResult(RESULT_OK);
        finish();
    }
    
    private void exitWithoutSave() {
    	mBoIsCancelled = true;
    	setResult(RESULT_OK);
        finish();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
        outState.putLong(NotesDbAdapter.KEY_PARENTID, mParentId);
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
        	
        	mTitleText.setText(R.string.NameEditor_TitleTextEdit);
        	
            Note note = mDbHelper.getNoteById(mRowId);
            
            mNameText.setText(note.getTitle());
            
            switch (note.getType()) {
            case Note.TYPE_FOLDER:
            	mTypeText.setText(this.getString(R.string.NameEditor_TypeText) + " " + this.getString(R.string.Commons_TypeFolder));
            	break;
            case Note.TYPE_NOTE:
            	mTypeText.setText(this.getString(R.string.NameEditor_TypeText) + " " + this.getString(R.string.Commons_TypeNote));
            	break;
            }
                        
            mModificationDateText.setText(this.getString(R.string.NameEditor_ModificationDateText) + " " + DateUtils.getDisplayDate(this, note.getModificationDate()));
            mCreationDateText.setText(this.getString(R.string.NameEditor_CreationDateText) + " " + DateUtils.getDisplayDate(this, note.getCreationDate()));
            
        } else {
        	mTitleText.setText(R.string.NameEditor_TitleTextCreateFolder);
        	mTypeText.setVisibility(View.GONE);
        	mModificationDateText.setVisibility(View.GONE);
        	mCreationDateText.setVisibility(View.GONE);
        }
    }
    
    private void saveState() {
    	if (!mBoIsCancelled) {
    		String title = mNameText.getText().toString();

    		if ((title == null) || 
    				(title.length() == 0)) {
    			title = getDefaultCategoryName();
    		}

    		if (mRowId == -1) {
    			long id = mDbHelper.createFolder(title, mParentId);
    			if (id > 0) {
    				mRowId = id;
    			}
    		} else {
    			mDbHelper.updateFolder(mRowId, title);
    		}        
    	}
    }
        
}
