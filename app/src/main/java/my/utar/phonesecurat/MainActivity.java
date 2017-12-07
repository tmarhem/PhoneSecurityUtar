package my.utar.phonesecurat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity that launches the application, heads directly to the menu.
 */
public class MainActivity extends Activity {

    final Context ctx = this;
    public static void requestSystemAlertPermission(Activity context, int requestCode) {
        final String packageName = context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
            context.startActivityForResult(intent, requestCode);
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

        if(!Settings.canDrawOverlays(this)){
            requestSystemAlertPermission(MainActivity.this,5463);
        }

        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(goToSettings, 1);
            }

        });

        mButtonStartBaseProfiling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                stopService(stopIntent);
                Intent goToStartBaseProfiling = new Intent(MainActivity.this, BaseProfilingActivity.class);
                startActivity(goToStartBaseProfiling);
            }

        });


        mButtonRunInBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
                boolean baseProfilingComplete = ( mPrefs.contains("mSwipeRightModel") ||
                        mPrefs.contains("mSwipeLeftModel") ||
                        mPrefs.contains("mScrollUpModel") ||
                        mPrefs.contains("mScrollDownModel") );
                if(!baseProfilingComplete) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Information");
                    alertDialogBuilder
                            .setMessage("The base profiling is incomplete, please fulfill " +
                                    "all first measurements to ensure the service is fully functional")
                            .setPositiveButton("Agreed", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent startIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                                    startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                                    startService(startIntent);
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else {
                    Intent startIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                    startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
                    startService(startIntent);
                }
                }
            }

        );

        mButtonStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, AuthenticationCheck.class);
                stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
                stopService(stopIntent);
            }

        });

    }
}


