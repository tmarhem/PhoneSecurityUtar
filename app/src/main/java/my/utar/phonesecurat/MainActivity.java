package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button mButtonSettings = null;
    private Button mButtonStartBaseProfiling = null;

    //TEST GITHUB CHGN

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Activity launch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSettings = (Button) findViewById(R.id.ButtonSettings);
        mButtonStartBaseProfiling = (Button) findViewById(R.id.ButtonStartBaseProfiling);


        mButtonSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(goToSettings);
            }

        });
        mButtonStartBaseProfiling.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent goToStartBaseProfiling = new Intent(MainActivity.this, BaseProfilingActivity.class);
                startActivity(goToStartBaseProfiling);
            }

        });
        //TODO remplissage basic other activity
        //TODO retreive swiping gesture

    }


}


