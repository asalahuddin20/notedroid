package org.notedroid.model;

import org.notedroid.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class ListDbAdapter extends SimpleCursorAdapter {

	private Bitmap mFolderIcon;
	private Bitmap mNoteIcon;
	
	private NotesDbAdapter mDbHelper;
	
	public ListDbAdapter(Context context, int layout, Cursor c, String[] from, int[] to, NotesDbAdapter dbHelper) {
		super(context, layout, c, from, to);
		
		mDbHelper = dbHelper;
		
		mFolderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder48);
		mNoteIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.note48);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View superView =  super.getView(position, convertView, parent);
		
		// Get current item id
		long id = this.getItemId(position);
		
		// Get the image field
		ImageView image = (ImageView) superView.findViewById(R.id.notesrow_Icon);
		
		// Set the correct image.
		int type = mDbHelper.getNoteById(id).getType();
		if (type == Note.TYPE_FOLDER) {
			image.setImageBitmap(mFolderIcon);
		} else {
			image.setImageBitmap(mNoteIcon);
		}		
		
		return superView;
	}

}
