package org.notedroid;

import org.notedroid.dialogs.AboutDialog;
import org.notedroid.gui.activities.NoteEditor;
import org.notedroid.gui.activities.NoteList;
import org.notedroid.gui.activities.PreferencesScreen;
import org.notedroid.model.NotesDbAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoteDroid extends Activity {
	
	public static final int ABOUT_DIALOG_ID = 0;
	
	private static final int ACTIVITY_VIEW_NOTE_LIST = 0;
	private static final int ACTIVITY_CREATE_NEW_NOTE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button viewNotesBtn = (Button) findViewById(R.id.Main_ViewNotesBtn);		
		viewNotesBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	openNoteList();
            }          
        });
		
		Button newNoteBtn = (Button) findViewById(R.id.Main_NewNoteBtn);
		newNoteBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	openNewNote();
            }          
        });
		
		Button preferencesBtn = (Button) findViewById(R.id.Main_ViewPreferencesBtn);
		preferencesBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	openPreferences();
            }          
        });
		
		Button aboutsBtn = (Button) findViewById(R.id.Main_ViewAboutBtn);
		aboutsBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	showDialog(ABOUT_DIALOG_ID);
            }          
        });
	}
	
	private void openNoteList() {
		Intent i = new Intent(this, NoteList.class);
		startActivityForResult(i, ACTIVITY_VIEW_NOTE_LIST);
	}
	
	private void openNewNote() {	
		Intent i = new Intent(this, NoteEditor.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, new Long(-1));
        i.putExtra(NotesDbAdapter.KEY_PARENTID, new Long(-1));
        i.putExtra(NoteEditor.NOTEEDITOR_MODE, NoteEditor.NOTEEDITOR_MODE_EDIT);                
		startActivityForResult(i, ACTIVITY_CREATE_NEW_NOTE);		
	}
	
	private void openPreferences() {
		Intent preferencesActivity = new Intent(this, PreferencesScreen.class);
  		startActivity(preferencesActivity);
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
	    switch(id) {
	    case ABOUT_DIALOG_ID:
	    	dialog = new AboutDialog(this);
	    	break;
	    }
	    return dialog;
	}
	
}