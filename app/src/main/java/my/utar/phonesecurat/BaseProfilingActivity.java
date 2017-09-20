package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import java.util.Vector;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class BaseProfilingActivity extends Activity {

    private VelocityTracker mVelocityTracker = null;
    private TextView mSpeedDisplay = null;
    private Vector mVector = null;
    private StructMotionElemts mStructMotionElemts = null;
    //instant speed
    private double speed;

    //StructMotionElemts required variables
    private long posX;
    private long posY;
    private long time;
    private double pressure;
    private double instantSpeed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        Intent i = getIntent();

        mSpeedDisplay = (TextView) findViewById(R.id.speedDisplay);

    }
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN :
                if (mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);

                //TODO Compute X, Y, time, pressure & instantSpeed
                //TODO create Struct from these values
                //TODO Insert object in vector
                mVector = new Vector();
                //mVector.add();
            break;

            case MotionEvent.ACTION_MOVE :
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                speed = sqrt(pow(mVelocityTracker.getXVelocity(),2) +
                        pow(mVelocityTracker.getYVelocity(),2));
                mSpeedDisplay.setText(String.valueOf( (int) speed));

                //TODO Compute X, Y, time, pressure & speed
                //TODO create Struct from these values
                //TODO Insert object in vector

        }
        return true;
    }
}

