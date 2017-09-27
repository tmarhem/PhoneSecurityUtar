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

    //Results
    private long motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;

    //Utility
    private double sumSpeed;
    private float firstPosX, lastPosX, firstPosY, lastPosY;
    private float prevPosX, prevPosY, nowPosX, nowPosY;
    private long  startTime, endTime;

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
                mVector.addElement(mStructMotionElemts.clone());
                Log.v("TEST","\nNEW ELEMENT\n");

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
                mVector.addElement(mStructMotionElemts.clone());
                mSpeedDisplay.setText(mStructMotionElemts.toString());
                break;

            case MotionEvent.ACTION_UP :
                //Sum reset
                sumSpeed = 0;
                motionLength = 0;
                //Iterator out of vector
                Iterator i = mVector.iterator();
                while (i.hasNext()){
                    mStructMotionElemts = (StructMotionElemts) i.next();
                    //Needed for avgSpeed calculation
                    sumSpeed += mStructMotionElemts.getSpeed();

                    //DISPLAY//////////////////////
                    Log.v("TEST",mStructMotionElemts.toString());
                    ///////////////////////////////
                }
                //TODO compute out of raw values
                //motionAvgSpeed
                motionAvgSpeed = sumSpeed / mVector.size() ;
                //motionDuration & motionAbsLength
                    //Retrieving
                mStructMotionElemts = (StructMotionElemts) mVector.firstElement();
                firstPosX = mStructMotionElemts.getPosX();
                firstPosY = mStructMotionElemts.getPosY();
                startTime = mStructMotionElemts.getTime();
                mStructMotionElemts = (StructMotionElemts) mVector.get(mVector.size()-1);
                lastPosX = mStructMotionElemts.getPosX();
                lastPosY = mStructMotionElemts.getPosY();
                endTime = mStructMotionElemts.getTime();
                    //Computing
                motionDuration = endTime - startTime;
                //motionAbsLength = sqrt(pow((las),2)+pow((),2));
            break;
        }
        return true;
    }
}

