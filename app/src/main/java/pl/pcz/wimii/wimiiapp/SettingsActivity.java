package pl.pcz.wimii.wimiiapp;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import pl.pcz.wimii.wimiiapp.data.DbHelper;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_CHANNEL = "pref_channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PREF_CHANNEL)) {
            Preference channelPref = findPreference(key);
            channelPref.setSummary(sharedPreferences.getString(key, ""));
            DbHelper mDbHelper = new DbHelper(getBaseContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(ReaderContract.NewsEntry.TABLE_NAME, null, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
