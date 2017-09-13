package my.utar.phonesecurat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button ButtonSettings = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Activity launch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonSettings = (Button) findViewById(R.id.ButtonSettings);

        ButtonSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void OnClick(View v){
                Intent goToSettings = new Intent(MainActivity.this , SettingsActivity.class);
                startActivity(goToSettings);
            }

        });
        //TODO  //DOING// Naviguation interactivity
        //TODO remplissage basic other activity
        //TODO retreive swiping gesture

    }

}

