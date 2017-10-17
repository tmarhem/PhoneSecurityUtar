package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    private StructMotionFeaturesList mModelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Activity launch
        Button mButtonSettings;
        Button mButtonStartBaseProfiling;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mModelList = new StructMotionFeaturesList();

        mButtonSettings = findViewById(R.id.ButtonSettings);
        mButtonStartBaseProfiling = findViewById(R.id.ButtonStartBaseProfiling);


        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(goToSettings);
            }

        });
        mButtonStartBaseProfiling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStartBaseProfiling = new Intent(MainActivity.this, BaseProfilingActivity.class);
                goToStartBaseProfiling.putExtra("mModelList",(Parcelable) mModelList);
                startActivity(goToStartBaseProfiling);
            }

        });

    }
}


