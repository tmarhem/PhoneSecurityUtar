package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Vector;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class BaseProfilingActivity extends Activity {

    private VelocityTracker mVelocityTracker = null;
    private TextView mSpeedDisplay = null;
    private Vector mVector = null;
    private StructMotionElemts mStructMotionElemts = null;

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
                //Creation ou reinitialisation du StructMotionElemts
                if (mStructMotionElemts == null){
                    mStructMotionElemts = new StructMotionElemts();
                }
                else {
                    mStructMotionElemts.clear();
                }

                // Compute X, Y, time, pressure & instantSpeed

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);

                mStructMotionElemts.setSpeed(sqrt(pow(mVelocityTracker.getXVelocity(),2) +
                        pow(mVelocityTracker.getYVelocity(),2)));
                mStructMotionElemts.setPosX(event.getX());
                mStructMotionElemts.setPosY(event.getY());
                mStructMotionElemts.setPressure(event.getPressure());
                mStructMotionElemts.setTime(SystemClock.uptimeMillis());
                // Insert object in vector
                mVector.addElement(mStructMotionElemts);
            break;

            case MotionEvent.ACTION_MOVE :
                // Compute X, Y, time, pressure & instantSpeed

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);

                mStructMotionElemts.setSpeed(sqrt(pow(mVelocityTracker.getXVelocity(),2) +
                        pow(mVelocityTracker.getYVelocity(),2)));
                mStructMotionElemts.setPosX(event.getX());
                mStructMotionElemts.setPosY(event.getY());
                mStructMotionElemts.setPressure(event.getPressure());
                mStructMotionElemts.setTime(SystemClock.uptimeMillis());
                // Insert object in vector
                mVector.addElement(mStructMotionElemts);
                break;

            case MotionEvent.ACTION_UP :
                Iterator i = mVector.iterator();
                while (i.hasNext()){
                    mStructMotionElemts = (StructMotionElemts) i.next();
                    Log.v("TEST",mStructMotionElemts.toString());
                }
            break;
        }
        return true;
    }
}

