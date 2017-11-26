package my.utar.phonesecurat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
 * Main activity that launches the application, heads directly to the menu
 */
public class MainActivity extends Activity {


    private UserModel mRightSwipeModel;

    /*private UserModel mLeftSwipeModel;
    private UserModel mScrollDownModel;
    private UserModel mScrollUpModel;*/

    ///
    public static void requestSystemAlertPermission(Activity context, int requestCode) {
        final String packageName = context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
            context.startActivityForResult(intent, requestCode);
    }
    ///

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRightSwipeModel = new UserModel();
        /*mLeftSwipeModel = new UserModel();
        mScrollDownModel = new UserModel();
        mScrollUpModel = new UserModel();*/
        Button mButtonSettings;
        Button mButtonStartBaseProfiling;
        Button mButtonRunInBackground;
        Button mButtonStopService;

        mButtonSettings = findViewById(R.id.ButtonSettings);
        mButtonStartBaseProfiling = findViewById(R.id.ButtonStartBaseProfiling);
        mButtonRunInBackground = findViewById(R.id.ButtonRunInBackground);
        mButtonStopService = findViewById(R.id.ButtonStopService);

        ///
        requestSystemAlertPermission(MainActivity.this,5463);
        ///

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
                Intent goToStartBaseProfiling = new Intent(MainActivity.this, BaseProfilingActivity.class);
                goToStartBaseProfiling.putExtra("mRightSwipeModel", mRightSwipeModel);
                //TODO Add other models
                startActivity(goToStartBaseProfiling);
            }

        });


        mButtonRunInBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authService = new Intent(MainActivity.this, AuthenticationCheck.class);
                startService(authService);
            }

        });

        mButtonStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authService = new Intent(MainActivity.this, AuthenticationCheck.class);
                stopService(authService);
            }

        });

    }
}


