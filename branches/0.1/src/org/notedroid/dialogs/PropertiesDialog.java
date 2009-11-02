package org.notedroid.dialogs;

import org.notedroid.R;
import org.notedroid.model.Note;
import org.notedroid.model.NotesDbAdapter;
import org.notedroid.utils.DateUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PropertiesDialog extends Dialog {

	private Context mCtx;
	
	public PropertiesDialog(Context context) {
		super(context);
		mCtx = context;
		
		setContentView(R.layout.propertydialog);
		setTitle(mCtx.getString(R.string.PropertiesDialog_Title));
	}
	
	public void prepareDialog(NotesDbAdapter dbHelper, long rowId) {
		Note note = dbHelper.getNoteById(rowId);		
		
		TextView text1 = (TextView) this.findViewById(R.id.PropertiesDialog_Text1);
		text1.setText(mCtx.getString(R.string.PropertiesDialog_Name) + " " + note.getTitle());
		
		TextView text2 = (TextView) this.findViewById(R.id.PropertiesDialog_Text2);
		switch (note.getType()) {
		case Note.TYPE_NOTE:
			text2.setText(mCtx.getString(R.string.PropertiesDialog_Type) + " " + mCtx.getString(R.string.Commons_TypeNote));
			break;
		case Note.TYPE_FOLDER:
			text2.setText(mCtx.getString(R.string.PropertiesDialog_Type) + " " + mCtx.getString(R.string.Commons_TypeFolder));
			break;
		}		
		
		TextView text3 = (TextView) this.findViewById(R.id.PropertiesDialog_Text3);
		text3.setText(mCtx.getString(R.string.PropertiesDialog_CreationDate) + " " + DateUtils.getDisplayDate(mCtx, note.getCreationDate()));
		
		TextView text4 = (TextView) this.findViewById(R.id.PropertiesDialog_Text4);
		text4.setText(mCtx.getString(R.string.PropertiesDialog_ModificationDate) + " " + DateUtils.getDisplayDate(mCtx, note.getModificationDate()));
		
		Button closeBtn = (Button) this.findViewById(R.id.PropertiesDialog_CloseBtn);
		closeBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {            	
            	dismiss();
            }
          
        });
	}

}
