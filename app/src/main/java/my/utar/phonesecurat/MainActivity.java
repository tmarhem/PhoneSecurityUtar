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
//WEKA Libs
import weka.core.*;
import weka.classifiers.functions.LibSVM;

import com.google.gson.Gson;


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

        mButtonSettings = findViewById(R.id.ButtonSettings);
        mButtonStartBaseProfiling = findViewById(R.id.ButtonStartBaseProfiling);
        mButtonRunInBackground = findViewById(R.id.ButtonRunInBackground);
        mButtonStopService = findViewById(R.id.ButtonStopService);

        //Authorization check
        if (!Settings.canDrawOverlays(this)) {
            requestSystemAlertPermission(MainActivity.this, 5463);
        }

        //RETRIEVING MODELS ON STARTUP
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
                //Shutting down the background service when going to BaseProfilingActivity
                Intent stopIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                stopService(stopIntent);

                Intent goToStartBaseProfiling = new Intent(MainActivity.this, BaseProfilingActivity.class);
                goToStartBaseProfiling.putExtra("mSwipeRightModel",mSwipeRightModel);
                goToStartBaseProfiling.putExtra("mSwipeLeftModel",mSwipeLeftModel);
                goToStartBaseProfiling.putExtra("mScrollUpModel",mScrollUpModel);
                goToStartBaseProfiling.putExtra("mScrollDownModel",mScrollDownModel);
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
                                                          startIntent.putExtra("mSwipeRightModel",mSwipeRightModel);
                                                          startIntent.putExtra("mSwipeLeftModel",mSwipeLeftModel);
                                                          startIntent.putExtra("mScrollUpModel",mScrollUpModel);
                                                          startIntent.putExtra("mScrollDownModel",mScrollDownModel);
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
                mSwipeRightModel = data.getParcelableExtra("mSwipeRightModel");
                mSwipeLeftModel = data.getParcelableExtra("mSwipeLeftModel");
                mScrollUpModel = data.getParcelableExtra("mScrollUpModel");
                mScrollDownModel = data.getParcelableExtra("mScrollDownModel");

                final SharedPreferences mPrefs = getSharedPreferences("mPrefs", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gsonSave = new Gson();
                String mSRM = gsonSave.toJson(mSwipeRightModel);
                String mSLM = gsonSave.toJson(mSwipeLeftModel);
                String mSUM = gsonSave.toJson(mScrollUpModel);
                String mSDM = gsonSave.toJson(mScrollDownModel);

                prefsEditor.putString("mSwipeRightModel", mSRM);
                prefsEditor.putString("mSwipeLeftModel", mSLM);
                prefsEditor.putString("mScrollUpModel", mSUM);
                prefsEditor.putString("mScrollDownModel", mSDM);

                prefsEditor.apply();
            }
        }
    }
}


