package org.notedroid.activities;

import org.notedroid.R;
import org.notedroid.model.ListDbAdapter;
import org.notedroid.model.NotesDbAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NoteList extends ListActivity {
	
	private static final int ACTIVITY_CREATE_NOTE = 0;
    private static final int ACTIVITY_EDIT_NOTE = 1;
    private static final int ACTIVITY_CREATE_FOLDER = 2;
    private static final int ACTIVITY_EDIT_NAME = 3;    
    
    private static final int INSERT_NOTE_ID = Menu.FIRST;
    private static final int DELETE_NOTE_ID = Menu.FIRST + 1;
    private static final int INSERT_FOLDER_ID = Menu.FIRST + 2;
    private static final int EDIT_NAME_ID = Menu.FIRST + 3;
    private static final int MOVE_FOLDER_UP_ID = Menu.FIRST + 4;
	
	private NotesDbAdapter mDbHelper;
	
	private long mCurrentParentId = -1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        fillData();
        
        registerForContextMenu(getListView());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem item;
        item = menu.add(0, INSERT_NOTE_ID, 0, R.string.menu_create_note);
        item.setIcon(R.drawable.newnote32);
        
        item = menu.add(0, INSERT_FOLDER_ID, 0, R.string.menu_create_folder);
        item.setIcon(R.drawable.newfolder32);
        
        item = menu.add(0, MOVE_FOLDER_UP_ID, 0, R.string.menu_move_folder_up);
        item.setIcon(R.drawable.moveup32);        
        
        return true;
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) { 
    		if (doMoveUp()) {
    			return false;
    		} else {
    			return super.onKeyDown(keyCode, event);
    		}
    	} else {
    		return super.onKeyDown(keyCode, event);
    	}
	}

	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) {
        case INSERT_NOTE_ID:
            createNote();
            return true;            
        case INSERT_FOLDER_ID:
        	createCategory();
        	return true;
        case MOVE_FOLDER_UP_ID:
        	if (!doMoveUp()) {
        		finish();
        	}
        	return true;
        }    	
       
        return super.onMenuItemSelected(featureId, item);
    }
	
	private boolean doMoveUp() {
		if (mCurrentParentId != -1) {
    		mCurrentParentId = mDbHelper.getParentById(mCurrentParentId);
    		fillData();
    		return true;
    	} else {
    		return false;
    	}
	}
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuItem item;
		item = menu.add(0, EDIT_NAME_ID, 0, R.string.menu_edit_name);
        item = menu.add(0, DELETE_NOTE_ID, 0, R.string.menu_delete);
        item.setIcon(R.drawable.delete32);
	}
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info;
    	
    	switch(item.getItemId()) {
    	case EDIT_NAME_ID:
    		info = (AdapterContextMenuInfo) item.getMenuInfo();
    		editName(info.id);
    		fillData();
    		return true;
    	case DELETE_NOTE_ID:
    		info = (AdapterContextMenuInfo) item.getMenuInfo();
	        mDbHelper.deleteNote(info.id);
	        fillData();
	        return true;
		}
		return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        int type = mDbHelper.getTypeById(id);

        if (type == NotesDbAdapter.TYPE_NOTE) {
        	Intent i = new Intent(this, NoteEditor.class);
        	i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        	startActivityForResult(i, ACTIVITY_EDIT_NOTE);
        } else if (type == NotesDbAdapter.TYPE_FOLDER) {
        	mCurrentParentId = id;
        	fillData();
        }
    }
    
    private void editName(Long rowId) {
    	Intent i = new Intent(this, NameEditor.class);
    	i.putExtra(NotesDbAdapter.KEY_ROWID, rowId);
    	i.putExtra(NotesDbAdapter.KEY_PARENTID, mCurrentParentId);
        startActivityForResult(i, ACTIVITY_EDIT_NAME);
    }
    
    private void createNote() {
        Intent i = new Intent(this, NoteEditor.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, new Long(-1));
        i.putExtra(NotesDbAdapter.KEY_PARENTID, mCurrentParentId);
        startActivityForResult(i, ACTIVITY_CREATE_NOTE);
    }
    
    private void createCategory() {
    	Intent i = new Intent(this, NameEditor.class);
    	i.putExtra(NotesDbAdapter.KEY_ROWID, new Long(-1));
    	i.putExtra(NotesDbAdapter.KEY_PARENTID, mCurrentParentId);
        startActivityForResult(i, ACTIVITY_CREATE_FOLDER);    	
    }
    
    private void fillData() {
    	Cursor notesCursor = mDbHelper.fetchNotesByParent(mCurrentParentId);
        startManagingCursor(notesCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] {NotesDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[] {R.id.notesrow_Title};
        
        // Now create a simple cursor adapter and set it to display
        ListDbAdapter notes = new ListDbAdapter(this, R.layout.notesrow, notesCursor, from, to, mDbHelper);
        setListAdapter(notes);    
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
