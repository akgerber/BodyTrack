package org.bodytrack.BodyTrack.Activities;

import org.bodytrack.BodyTrack.R;
import org.bodytrack.BodyTrack.R.xml;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * This class creates an activity to modify preferences.
 * Preferences set are defined in res/xml/prefs.xml
 * 
 */

public class BTPrefs extends PreferenceActivity {
	public static final String TAG = "BTPrefs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Load preferences from XML
		addPreferencesFromResource(R.xml.prefs);
	}

}
