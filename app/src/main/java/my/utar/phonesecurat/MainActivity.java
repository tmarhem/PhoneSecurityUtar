package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity that launches the application, heads directly to the menu
 */
public class MainActivity extends Activity {


    private UserModel mRightSwipeModel;
    private UserModel mLeftSwipeModel;
    private UserModel mScrollDownModel;
    private UserModel mScrollUpModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRightSwipeModel = new UserModel();
        mLeftSwipeModel = new UserModel();
        mScrollDownModel = new UserModel();
        mScrollUpModel = new UserModel();
        Button mButtonSettings;
        Button mButtonStartBaseProfiling;

        mButtonSettings = findViewById(R.id.ButtonSettings);
        mButtonStartBaseProfiling = findViewById(R.id.ButtonStartBaseProfiling);


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

    }
}


