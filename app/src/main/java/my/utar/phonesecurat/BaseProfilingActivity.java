package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.webkit.ConsoleMessage;
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
    private float posX;
    private float posY;
    private long time;
    private float pressure;
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
                //Creation ou reinitialisation du VelocityTracker
                if (mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    mVelocityTracker.clear();
                }
                //Creation ou reinitialisation du Vector
                if (mVector == null){
                    mVector = new Vector();
                }
                else {
                    mVector.clear();
                }

                // Compute X, Y, time, pressure & instantSpeed

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);

                instantSpeed = sqrt(pow(mVelocityTracker.getXVelocity(),2) +
                        pow(mVelocityTracker.getYVelocity(),2));
                posX = event.getX();
                posY = event.getY();
                pressure = event.getPressure();
                time = SystemClock.uptimeMillis();
                // create Struct from these values
                // Insert object in vector
                mVector.add(new StructMotionElemts(posX, posY, time, pressure, instantSpeed));
            break;

            case MotionEvent.ACTION_MOVE :
                // Compute X, Y, time, pressure & instantSpeed

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);

                instantSpeed = sqrt(pow(mVelocityTracker.getXVelocity(),2) +
                        pow(mVelocityTracker.getYVelocity(),2));
                posX = event.getX();
                posY = event.getY();
                pressure = event.getPressure();
                time = SystemClock.uptimeMillis();
                // create Struct from these values
                // Insert object in vector
                mVector.add(new StructMotionElemts(posX, posY, time, pressure, instantSpeed));
                break;

            case MotionEvent.ACTION_UP :
                //mStructMotionElemts = mVector.firstElement();
            break;
        }
        return true;
    }
}

