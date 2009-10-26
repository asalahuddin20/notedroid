package org.notedroid.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.notedroid.R;

import android.content.Context;
import android.util.Log;

public class DateUtils {
	
	public static String getNow(Context context) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.DATE_FORMAT_ISO8601));				
		
		return sdf.format(c.getTime());
	} 
	
	public static String getDisplayDate(Context context, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.DATE_FORMAT_ISO8601));
		return sdf.format(date);
	}
	
	public static Date convertFromDatabase(Context context, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.DATE_FORMAT_ISO8601));
		
		try {
			
			return sdf.parse(date);
			
		} catch (ParseException e) {
			Log.w(DateUtils.class.toString(), "Error parsing date (" + date + "): " + e.getMessage());
			
			return new Date();
		}
	}

}
