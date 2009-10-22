package org.notedroid;

import org.notedroid.activities.NoteEditor;
import org.notedroid.activities.NoteList;
import org.notedroid.activities.PreferencesScreen;
import org.notedroid.model.NotesDbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class NoteDroid extends Activity {
	
	private static final int ACTIVITY_VIEW_NOTE_LIST = 0;
	private static final int ACTIVITY_CREATE_NEW_NOTE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ImageButton viewNotesBtn = (ImageButton) findViewById(R.id.Main_ViewNotesBtn);		
		viewNotesBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	openNoteList();
            }          
        });
		
		ImageButton newNoteBtn = (ImageButton) findViewById(R.id.Main_NewNoteBtn);
		newNoteBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	openNewNote();
            }          
        });
		
		ImageButton preferencesBtn = (ImageButton) findViewById(R.id.Main_ViewPreferencesBtn);
		preferencesBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	openPreferences();
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
		startActivityForResult(i, ACTIVITY_CREATE_NEW_NOTE);		
	}
	
	private void openPreferences() {
		Intent preferencesActivity = new Intent(this, PreferencesScreen.class);
  		startActivity(preferencesActivity);

	}
	
}