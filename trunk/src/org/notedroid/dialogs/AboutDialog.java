package org.notedroid.dialogs;

import org.notedroid.R;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	
	private Context mCtxt;

	public AboutDialog(Context context) {
		super(context);
		
		mCtxt = context;
		
		setContentView(R.layout.aboutdialog);
		
		setTitle(mCtxt.getString(R.string.AboutDialog_Title) + " " + mCtxt.getString(R.string.Notedroid_ApplicationName));
		
		TextView versionText = (TextView) this.findViewById(R.id.AboutDialog_VersionText);
		versionText.setText(mCtxt.getString(R.string.AboutDialog_VersionText) + " " + getVersion());
		
		TextView licenseText = (TextView) this.findViewById(R.id.AboutDialog_LicenseText);
		licenseText.setText(mCtxt.getString(R.string.AboutDialog_LicenseText) + " " + mCtxt.getString(R.string.AboutDialog_LicenseTextValue));
		
		TextView urlText = (TextView) this.findViewById(R.id.AboutDialog_UrlText);
		urlText.setText(mCtxt.getString(R.string.AboutDialog_UrlTextValue));				
		
		Button closeBtn = (Button) this.findViewById(R.id.AboutDialog_CloseBtn);
		closeBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {            	
            	dismiss();
            }
          
        });

	}
	
	private String getVersion() {		
		String result = "";		
        try {
        	
        	PackageManager manager = mCtxt.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mCtxt.getPackageName(), 0);
			
			result = info.versionName;
			
		} catch (NameNotFoundException e) {
			Log.w(AboutDialog.class.toString(), "Unable to get application version: " + e.getMessage());
		}
		
		return result;
	}

}
