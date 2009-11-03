package org.notedroid.gui.activities;

import org.notedroid.R;
import org.notedroid.dialogs.PropertiesDialog;
import org.notedroid.gui.ListAdapter;
import org.notedroid.model.Note;
import org.notedroid.model.NotesDbAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
    
    private static final int MENU_INSERT_NOTE_ID = Menu.FIRST;
    private static final int MENU_DELETE_NOTE_ID = Menu.FIRST + 1;
    private static final int MENU_INSERT_FOLDER_ID = Menu.FIRST + 2;
    private static final int MENU_EDIT_NAME_ID = Menu.FIRST + 3;
    private static final int MENU_MOVE_FOLDER_UP_ID = Menu.FIRST + 4;
    private static final int MENU_SHOW_PROPERTIES_ID = Menu.FIRST + 5;
	
	private NotesDbAdapter mDbHelper;
	
	private long mCurrentParentId = -1;
	private long mCurrentClickedId = -1;
	
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
        item = menu.add(0, MENU_INSERT_NOTE_ID, 0, R.string.NoteList_MenuCreateNote);
        item.setIcon(R.drawable.newnote32);
        
        item = menu.add(0, MENU_INSERT_FOLDER_ID, 0, R.string.NoteList_MenuCreateFolder);
        item.setIcon(R.drawable.newfolder32);
        
        item = menu.add(0, MENU_MOVE_FOLDER_UP_ID, 0, R.string.NoteList_MenuMoveUp);
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
        case MENU_INSERT_NOTE_ID:
            createNote();
            return true;            
        case MENU_INSERT_FOLDER_ID:
        	createFolder();
        	return true;
        case MENU_MOVE_FOLDER_UP_ID:
        	if (!doMoveUp()) {
        		finish();
        	}
        	return true;
        }    	
       
        return super.onMenuItemSelected(featureId, item);
    }
	
	private boolean doMoveUp() {
		if (mCurrentParentId != -1) {
			mCurrentParentId = mDbHelper.getNoteById(mCurrentParentId).getParentId();
    		fillData();
    		return true;
    	} else {
    		return false;
    	}
	}
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		long selectedId = ((AdapterContextMenuInfo) menuInfo).id;
		
		if (selectedId != -1) {
			menu.setHeaderTitle(mDbHelper.getNoteById(selectedId).getTitle());
		}		
		
		menu.add(0, MENU_EDIT_NAME_ID, 0, R.string.NoteList_MenuEditName);
        menu.add(0, MENU_DELETE_NOTE_ID, 0, R.string.NoteList_MenuDelete);
        menu.add(0, MENU_SHOW_PROPERTIES_ID, 0, R.string.NoteList_MenuShowProperties);
        
	}
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info;    	    	
    	
    	switch(item.getItemId()) {
    	case MENU_EDIT_NAME_ID:
    		info = (AdapterContextMenuInfo) item.getMenuInfo();
    		editName(info.id);
    		fillData();
    		return true;
    	case MENU_DELETE_NOTE_ID:
    		info = (AdapterContextMenuInfo) item.getMenuInfo();
    		doDeleteNote(info.id);	        
	        return true;
	    case MENU_SHOW_PROPERTIES_ID:
	    	info = (AdapterContextMenuInfo) item.getMenuInfo();
	    	mCurrentClickedId = info.id;
	    	showDialog(MENU_SHOW_PROPERTIES_ID);
	    	return true;
		}
		return super.onContextItemSelected(item);
    }
    
    private void doDeleteNote(final long rowId) {
    	String message;
    	int noteType = mDbHelper.getNoteType(rowId);
    	if (noteType == Note.TYPE_FOLDER) {
    		message = this.getString(R.string.NoteList_DeleteFolderQuestion);
    	} else {
    		message = this.getString(R.string.NoteList_DeleteNoteQuestion);
    	}
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message);
    	builder.setCancelable(false);
    	builder.setPositiveButton(this.getString(R.string.Commons_Yes), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			mDbHelper.deleteNote(rowId);
    			fillData();
    		}
    	});
    	builder.setNegativeButton(this.getString(R.string.Commons_No), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.cancel();
    		}
    	});

    	builder.create().show();
    	
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);                       
        
        int type = mDbHelper.getNoteType(id);

        if (type == Note.TYPE_NOTE) {
        	Intent i = new Intent(this, NoteEditor.class);
        	i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        	i.putExtra(NoteEditor.NOTEEDITOR_MODE, NoteEditor.NOTEEDITOR_MODE_SHOW);
        	startActivityForResult(i, ACTIVITY_EDIT_NOTE);
        } else if (type == Note.TYPE_FOLDER) {
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
        i.putExtra(NoteEditor.NOTEEDITOR_MODE, NoteEditor.NOTEEDITOR_MODE_EDIT);
        startActivityForResult(i, ACTIVITY_CREATE_NOTE);
    }
    
    private void createFolder() {
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
        ListAdapter notes = new ListAdapter(this, R.layout.notesrow, notesCursor, from, to, mDbHelper);
        setListAdapter(notes);    
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
	    switch(id) {
	    case MENU_SHOW_PROPERTIES_ID:
	    	dialog = new PropertiesDialog(this);
	    	break;
	    }
	    return dialog;
	}
    
    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
    	 switch (id) {
    	 case MENU_SHOW_PROPERTIES_ID:
    		 ((PropertiesDialog) dialog).prepareDialog(mDbHelper, mCurrentClickedId);
    		 break;
    	 }
    }

}
