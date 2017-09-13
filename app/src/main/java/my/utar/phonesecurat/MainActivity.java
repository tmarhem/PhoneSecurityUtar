package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button mButtonSettings = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Activity launch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSettings = (Button) findViewById(R.id.ButtonSettings);

        mButtonSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void OnClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(goToSettings);
            }

        });
        //TODO  //DOING// Naviguation interactivity
        //TODO remplissage basic other activity
        //TODO retreive swiping gesture

    }


}

