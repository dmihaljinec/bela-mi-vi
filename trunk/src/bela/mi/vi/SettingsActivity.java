package bela.mi.vi;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SettingsActivity  extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		initSummaries(getPreferenceScreen());

	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	private void initSummaries(PreferenceGroup prefGroup) {

		for (int i = 0; i < prefGroup.getPreferenceCount(); ++i) {
			Preference pref = prefGroup.getPreference(i);
			if (pref instanceof PreferenceGroup)
				initSummaries((PreferenceGroup) pref);
			else
				setSummary(pref);
		}
	}

	private void setSummary(Preference pref) {

		if (pref instanceof EditTextPreference) {
			EditTextPreference editPref = (EditTextPreference) pref;
			pref.setSummary(editPref.getText());
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
		
		Preference pref = findPreference(key);
		setSummary(pref);
	}
}

