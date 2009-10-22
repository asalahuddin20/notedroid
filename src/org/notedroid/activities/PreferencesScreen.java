package org.notedroid.activities;

import org.notedroid.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesScreen extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	}

}
