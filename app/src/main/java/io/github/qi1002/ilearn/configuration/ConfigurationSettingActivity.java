package io.github.qi1002.ilearn.configuration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import io.github.qi1002.ilearn.DatasetRecord;
import io.github.qi1002.ilearn.Helper;
import io.github.qi1002.ilearn.R;

public class ConfigurationSettingActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.config_settings));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
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

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToBooleanListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(final Preference preference, Object value) {

            if (!(value instanceof Boolean))
                return true;

            Boolean boolValue = (Boolean)value;

            if (preference.hasKey() && preference.getKey().compareTo("dataset_update") == 0)
            {
                if (boolValue && DatasetRecord.getDataset().size() == 0) {

                    DialogInterface.OnClickListener positiveListner =
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    DatasetRecord.downloadDataset(preference.getContext(), DatasetRecord.dataset_filename);
                                }
                            };

                    Helper.SelectionBox(preference.getContext(), "Download dataset now ?", "Yes", "No",
                            "Dataset Update Selection Box", positiveListner, null);
                }
            }

            if (preference.hasKey() && preference.getKey().compareTo("dataset_check") == 0)
            {
                if (boolValue) {
                    Toast.makeText(preference.getContext(), "dataset check will work in the next iLearn Startup", Toast.LENGTH_SHORT).show();
                }
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
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "")); // default value defined in XML, so "" can be ignored
    }

    private static void bindPreferenceSummaryToBoolean(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToBooleanListener);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GlobalPreferenceFragment.class.getName().equals(fragmentName)
                || LookupPreferenceFragment.class.getName().equals(fragmentName)
                || PracticePreferenceFragment.class.getName().equals(fragmentName)
                || VocExamPreferenceFragment.class.getName().equals(fragmentName)
                || PronunExamPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /**
     * This fragment shows global preferences only.
     */
    public static class GlobalPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_global);
            setHasOptionsMenu(true);

            ConfigurationSettingActivity activity = (ConfigurationSettingActivity)getActivity();
            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                   actionBar.setTitle(getString(R.string.pref_header_global));
            }
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToBoolean(findPreference("dataset_update"));
            bindPreferenceSummaryToBoolean(findPreference("dataset_check"));
            bindPreferenceSummaryToValue(findPreference("dataset_url_location"));
            bindPreferenceSummaryToValue(findPreference("behavior_wait_voice"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), ConfigurationSettingActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows practice preferences only.
     */
    public static class LookupPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_lookup);
            setHasOptionsMenu(true);

            ConfigurationSettingActivity activity = (ConfigurationSettingActivity)getActivity();
            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                    actionBar.setTitle(getString(R.string.pref_header_lookup));
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), ConfigurationSettingActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows practice preferences only.
     */
    public static class PracticePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_practice);
            setHasOptionsMenu(true);

            ConfigurationSettingActivity activity = (ConfigurationSettingActivity)getActivity();
            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                    actionBar.setTitle(getString(R.string.pref_header_practice));
            }
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("practice_enumerate"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), ConfigurationSettingActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows vocabulary exam  preferences only.
     */
    public static class VocExamPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_vocabulary_exam);
            setHasOptionsMenu(true);

            ConfigurationSettingActivity activity = (ConfigurationSettingActivity)getActivity();
            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                    actionBar.setTitle(getString(R.string.pref_header_vocabulary_exam));
            }
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("vocabulary_exam_count"));
            bindPreferenceSummaryToValue(findPreference("vocabulary_exam_enumerate"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), ConfigurationSettingActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows pronunciation exam preferences only.
     */
    public static class PronunExamPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_pronunciation_exam);
            setHasOptionsMenu(true);

            ConfigurationSettingActivity activity = (ConfigurationSettingActivity)getActivity();
            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                    actionBar.setTitle(getString(R.string.pref_header_pronunciation_exam));
            }
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("pronunciation_exam_count"));
            bindPreferenceSummaryToValue(findPreference("pronunciation_exam_enumerate"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), ConfigurationSettingActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
