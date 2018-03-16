package com.shizhi.usp.setting;


import android.annotation.TargetApi;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.shizhi.usp.R;
import com.shizhi.usp.util.SerialPortFinder;

import java.util.Arrays;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {

    private DataSyncPreferenceFragment mDataSyncPreferenceFragment;
    private static String sLastActName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sLastActName = getIntent().getStringExtra("activity");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (mDataSyncPreferenceFragment == null) {
            mDataSyncPreferenceFragment = new DataSyncPreferenceFragment();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mDataSyncPreferenceFragment, mDataSyncPreferenceFragment.getTag()).commit();
        } else {
            getFragmentManager().beginTransaction().show(mDataSyncPreferenceFragment).commit();
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {

        ListPreference mNamePreferece;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            mNamePreferece = (ListPreference) findPreference("name");
            mNamePreferece.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, String[]>() {
                        @Override
                        protected String[] doInBackground(Object... params) {
                            if ("com.shizhi.usp.usb.UsbTestFra".equals(sLastActName)) {
                                final UsbManager maanager = (UsbManager) getActivity().getSystemService(USB_SERVICE);
                                if (maanager == null) {
                                    return new String[0];
                                }
                                final String[] ret = new String[maanager.getDeviceList().values().size()];
                                int index = -1;
                                for (UsbDevice device : maanager.getDeviceList().values()) {
                                    index++;
                                    ret[index] = device.getDeviceName();
                                }
                                return ret;
                            } else if ("com.shizhi.usp.serialport.SerialTestFra".equals(sLastActName)) {
                                return new SerialPortFinder().getAllDevicesPath();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String[] strings) {
                            super.onPostExecute(strings);
                            if (strings != null) {
                                Log.d(TAG, "onPostExecute: " + Arrays.toString(strings));
                                mNamePreferece.setEntries(strings);
                                mNamePreferece.setEntryValues(strings);
                            }
                        }
                    });
                    return true;
                }
            });

            bindPreferenceSummaryToValue(mNamePreferece);

            bindPreferenceSummaryToValue(findPreference("braunte"));

            bindPreferenceSummaryToValue(findPreference("data"));

            bindPreferenceSummaryToValue(findPreference("stop"));

            bindPreferenceSummaryToValue(findPreference("parity"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private static final String TAG = "DataSyncPreferenceFragm";
    }


}
