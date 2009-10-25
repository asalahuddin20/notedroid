package org.notedroid.dialogs;

import org.notedroid.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutDialog extends Dialog {

	public AboutDialog(Context context) {
		super(context);
		setContentView(R.layout.aboutdialog);
		
		setTitle(context.getString(R.string.AboutDialog_Title) + " " + context.getString(R.string.app_name));
		
		TextView text1 = (TextView) this.findViewById(R.id.AboutDialog_Text1);
		text1.setText(context.getString(R.string.AboutDialog_Text1) + " " + context.getString(R.string.AboutDialog_Version));
		
		TextView text2 = (TextView) this.findViewById(R.id.AboutDialog_Text2);
		text2.setText(R.string.AboutDialog_Text2);
		
		Button closeBtn = (Button) this.findViewById(R.id.AboutDialog_CloseBtn);
		closeBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {            	
            	dismiss();
            }
          
        });

	}

}
