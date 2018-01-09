package my.utar.phonesecurat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

/**
 * Main activity that launches the application, heads directly to the menu.
 * TODO 09.01 Managing save profiles from the MainActivity
 * TODO 20.12 Managing BackButton not working while trying to steal a move
 * TODO 9.01 Replacing that by shutting the capture while touching the bottom of the device
 * TODO 20.12 visual style of the first menu
 * TODO 20.12 Classifier
 */
public class MainActivity extends Activity {

    final Context ctx = this;

    public static void requestSystemAlertPermission(Activity context, int requestCode) {
        final String packageName = context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        context.startActivityForResult(intent, requestCode);
    }

    boolean sSR, sSL, sSD, sSU;
    UserModel mSwipeRightModel, mSwipeLeftModel, mScrollUpModel, mScrollDownModel;

    @Override
    public void onBackPressed() {
        // Write your code here
        Log.v("TEST", "Back pressed");
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mButtonSettings;
        Button mButtonStartBaseProfiling;
        Button mButtonRunInBackground;
        Button mButtonStopService;

        if (!Settings.canDrawOverlays(this)) {
            requestSystemAlertPermission(MainActivity.this, 5463);
        }

        mButtonSettings = findViewById(R.id.ButtonSettings);
        mButtonStartBaseProfiling = findViewById(R.id.ButtonStartBaseProfiling);
        mButtonRunInBackground = findViewById(R.id.ButtonRunInBackground);
        mButtonStopService = findViewById(R.id.ButtonStopService);

        //RETRIEVING MODELS ON STARTUP
        final SharedPreferences mPrefs = getSharedPreferences("mPrefs", MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        Log.v("TEST", Boolean.toString(mPrefs.contains("mSwipeRightModel")));
        Log.v("TEST", Boolean.toString(mPrefs.contains("mSwipeLeftModel")));
        Log.v("TEST", Boolean.toString(mPrefs.contains("mScrollUpModel")));
        Log.v("TEST", Boolean.toString(mPrefs.contains("mScrollDownModel")));

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


        mButtonSettings.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(goToSettings, 1);
            }

        });

        mButtonStartBaseProfiling.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                stopService(stopIntent);
                Intent goToStartBaseProfiling = new Intent(MainActivity.this, BaseProfilingActivity.class);
                startActivityForResult(goToStartBaseProfiling, Constants.REQUEST_CODE.MODELS_RETRIEVING);
            }

        });


        mButtonRunInBackground.setOnClickListener(new View.OnClickListener()
                                                  {
                                                      @Override
                                                      public void onClick(View v) {
                                                          boolean baseProfilingComplete = sSR && sSL && sSU && sSD;
                                                          if (!baseProfilingComplete) {
                                                              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                                                              alertDialogBuilder.setTitle("Information");
                                                              alertDialogBuilder
                                                                      .setMessage("The base profiling is incomplete, please fulfill " +
                                                                              "all first measurements to ensure the service is fully functional")
                                                                      .setPositiveButton("Agreed", new DialogInterface.OnClickListener() {
                                                                          public void onClick(DialogInterface dialog, int which) {
                                                                              dialog.dismiss();
                                                                          }
                                                                      });
                                                              AlertDialog alertDialog = alertDialogBuilder.create();
                                                              alertDialog.show();
                                                          }
                                                          Intent startIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                                                          startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                                                          startService(startIntent);
                                                      }
                                                  }

        );

        mButtonStopService.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                stopService(stopIntent);
            }

        });

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE.MODELS_RETRIEVING) {
            if (resultCode == RESULT_OK) {
                Bundle mBundle = data.getExtras();
                mBundle.getParcelable("mSwipeRightModel");
            }

        }

    }
}


