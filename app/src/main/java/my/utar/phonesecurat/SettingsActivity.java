package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
//WEKA Libs
import weka.core.*;
import weka.classifiers.functions.LibSVM;

import com.google.gson.Gson;

/**
 * Main activity that launches the application, heads directly to the settings menu.
 * TODO 20.12 Managing BackButton not working while trying to steal a move
 * TODO 9.01 Replacing that by shutting the capture while touching the bottom of the device
 * TODO 13.01 finish new settings menu
 * TODO 20.12 Classifier
 * Through WEKA, Use LibSVM
 * Process Idea : Use WEKA windows app for testing features using extract from 'EXPORT ONLY' version of app
 * Through empirical test, find the global settings parameters for our one class classifier
 * Implement it through Android
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    //Switches to check if basic training has been done
    boolean sSR, sSL, sSD, sSU;
    //User models
    UserModel mSwipeRightModel, mSwipeLeftModel, mScrollUpModel, mScrollDownModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Basic init
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        //Authorization check
        if (!Settings.canDrawOverlays(this)) {
            requestSystemAlertPermission(SettingsActivity.this, 5463);
        }

        //Retrieving user models on startup
        final SharedPreferences mPrefs = getSharedPreferences("mPrefs", MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String mSRM = mPrefs.getString("mSwipeRightModel", "");
        String mSLM = mPrefs.getString("mSwipeLeftModel", "");
        String mSUM = mPrefs.getString("mScrollUpModel", "");
        String mSDM = mPrefs.getString("mScrollDownModel", "");
        mSwipeRightModel = gsonLoad.fromJson(mSRM, UserModel.class);
        mSwipeLeftModel = gsonLoad.fromJson(mSLM, UserModel.class);
        mScrollUpModel = gsonLoad.fromJson(mSUM, UserModel.class);
        mScrollDownModel = gsonLoad.fromJson(mSDM, UserModel.class);
        sSR = false;
        sSL = false;
        sSU = false;
        sSD = false;

        if (mSwipeRightModel != null) {
            if (mSwipeRightModel.getIsComputed() == 1) {
                sSR = true;
            }
        } else mSwipeRightModel = new UserModel();

        if (mSwipeLeftModel != null) {
            if (mSwipeLeftModel.getIsComputed() == 1) {
                sSL = true;
            }
        } else mSwipeLeftModel = new UserModel();

        if (mScrollUpModel != null) {
            if (mScrollUpModel.getIsComputed() == 1) {
                sSU = true;
            }
        } else mScrollUpModel = new UserModel();

        if (mScrollDownModel != null) {
            if (mScrollDownModel.getIsComputed() == 1) {
                sSD = true;
            }
        } else mScrollDownModel = new UserModel();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchKey = prefs.getBoolean("switchKey", false);
        if (switchKey) {
            Intent i1 = new Intent(this, AuthenticationCheck.class);
            i1.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
            this.startService(i1);
        }
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            resetSwitchKey();

            // send feedback preference click listener
            final Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            //Tun in background switch preference click listener
            final Preference myPref2 = findPreference("switchKey");
            myPref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    //Retrieve switch value
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    boolean switchKey = prefs.getBoolean("switchKey", false);

                    Intent i = new Intent(getActivity(), AuthenticationCheck.class);

                    if (switchKey) {
                        i.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                        getActivity().startService(i);
                    } else {
                        i.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                        getActivity().stopService(i);

                    }
                    return true;
                }
            });

            // startBaseProfiling preference click listener
            final Preference myPref3 = findPreference("settings_startBaseProfiling");
            myPref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //Stop the background activity if running
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    boolean switchKey = prefs.getBoolean("switchKey", false);
                    if (switchKey) {
                        Intent i1 = new Intent(getActivity(), AuthenticationCheck.class);
                        i1.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                        getActivity().stopService(i1);
                    }
                    Intent i = new Intent(getActivity(), BaseProfilingActivity.class);
                    getActivity().startActivityForResult(i, 9876);
                    return true;
                }
            });
        }

        @Override
        public void onDestroy(){
            resetSwitchKey();
            super.onDestroy();
        }

        public void resetSwitchKey() {
            SharedPreferences.Editor editor = findPreference("switchKey").getEditor();
            editor.putBoolean("switchKey",false);
            editor.commit();
        }

    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"t.marhem@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }


    /**
     * Checks permissions, ak for them if missing
     *
     * @param context     context
     * @param requestCode code related to the request
     */
    public static void requestSystemAlertPermission(Activity context, int requestCode) {
        final String packageName = context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("TEST", " Inside onActivityResult");

        if (requestCode == 9876) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean switchKey = prefs.getBoolean("switchKey", false);
            if (switchKey) {
                Intent i1 = new Intent(this, AuthenticationCheck.class);
                i1.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                this.startService(i1);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        Intent i1 = new Intent(this, AuthenticationCheck.class);
        i1.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
        this.stopService(i1);
        super.onDestroy();


    }
}

