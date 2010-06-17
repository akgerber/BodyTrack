package org.bodytrack.BodyTrack;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class BTPrefs extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Load preferences from XML
		addPreferencesFromResource(R.xml.prefs);
	}

}
