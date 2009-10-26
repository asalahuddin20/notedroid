package org.notedroid.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.notedroid.R;
import org.notedroid.preferences.PreferencesConstants;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class DateUtils {
	
	private static String getDisplayFormat(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesConstants.DATE_DISPLAY_FORMAT, PreferencesConstants.DATE_DISPLAY_DEFAULT_VALUE);
	}
	
	private static String getDefaultFormat(Context context) {
		return context.getResources().getString(R.string.DATE_FORMAT_ISO8601);
	}
	
	public static String getNow(Context context) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(getDefaultFormat(context));				
		
		return sdf.format(c.getTime());
	} 
	
	public static String getDisplayDate(Context context, Date date) {
		SimpleDateFormat sdf;
		
		try {
			sdf = new SimpleDateFormat(getDisplayFormat(context));
		} catch (Exception e) {
			sdf = new SimpleDateFormat(getDefaultFormat(context));
		}
		return sdf.format(date);
	}
	
	public static Date convertFromDatabase(Context context, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(getDefaultFormat(context));
		
		try {
			
			return sdf.parse(date);
			
		} catch (ParseException e) {
			Log.w(DateUtils.class.toString(), "Error parsing date (" + date + "): " + e.getMessage());
			
			return new Date();
		}
	}

}
