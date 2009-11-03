package org.notedroid.utils;

import android.content.Context;
import android.widget.Toast;

public class ApplicationUtils {
	
	public static void showToasterNotification(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

}
