package my.utar.phonesecurat;
/**
Activity for model feature extraction
//TODO Size not working ?
//TODO ADDITIONNAL Draw the swipe
//TODO Swipe, scroll and touch classification
//TODO Left and Right recognition
        */
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
    private TextView mMotionInfo = null;
    private TextView mSpeedDisplay = null;
    private Vector mVector = null;
    private StructMotionElemts mStructMotionElemts = null;

    //Results
    private double motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;
    private double motionAvgPressure;

    //Utility
    private double sumSpeed, sumPressure;
    private float firstPosX, lastPosX, firstPosY, lastPosY;
    private float prevPosX, prevPosY, nowPosX, nowPosY;
    private long startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        Intent i = getIntent();

        mSpeedDisplay = (TextView) findViewById(R.id.speedDisplay);
        mMotionInfo = (TextView) findViewById(R.id.motionInfo);

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //Creation ou reinitialisation du VelocityTracker
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                //Creation ou reinitialisation du Vector
                if (mVector == null) {
                    mVector = new Vector();
                } else {
                    mVector.clear();
                }
                //Creation ou reinitialisation du StructMotionElemts
                if (mStructMotionElemts == null) {
                    mStructMotionElemts = new StructMotionElemts();
                } else {
                    mStructMotionElemts.clear();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                // Compute X, Y, time, pressure & instantSpeed

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);

                mStructMotionElemts.setSpeed(sqrt(pow(mVelocityTracker.getXVelocity(), 2) +
                        pow(mVelocityTracker.getYVelocity(), 2)));
                mStructMotionElemts.setPosX(event.getX());
                mStructMotionElemts.setPosY(event.getY());
                mStructMotionElemts.setPressure(event.getPressure());
                mStructMotionElemts.setSize(event.getSize());
                mStructMotionElemts.setTime(SystemClock.uptimeMillis());
                // Insert object in vector
                mVector.addElement(mStructMotionElemts.clone());
                mSpeedDisplay.setText(mStructMotionElemts.toString());
                break;

            //Exiting event, going though the vector by its iterator and making calulations
            case MotionEvent.ACTION_UP:
                //Resets and utility
                sumSpeed = 0;
                sumPressure = 0;
                motionLength = 0;
                //Initialisation bourrine de nowPos
                if (!mVector.isEmpty()) {

                    mStructMotionElemts = (StructMotionElemts) mVector.firstElement();
                    nowPosX = mStructMotionElemts.getPosX();
                    nowPosY = mStructMotionElemts.getPosY();
                    mStructMotionElemts.clear();

                    mVector.remove(0);

                    //Iterator out of vector
                    Iterator i = mVector.iterator();

                    while (i.hasNext()) {
                        prevPosX = nowPosX;
                        prevPosY = nowPosY;
                        mStructMotionElemts = (StructMotionElemts) i.next();
                        //motionLength calculation
                        nowPosX = mStructMotionElemts.getPosX();
                        nowPosY = mStructMotionElemts.getPosY();

                        motionLength += sqrt(pow((prevPosX - nowPosX), 2) + pow((prevPosY - nowPosY), 2));
                        //avgSpeed calculation
                        sumSpeed += mStructMotionElemts.getSpeed();
                        sumPressure += mStructMotionElemts.getPressure();

                        //DISPLAY//////////////////////
                        //Log.v("TEST", mStructMotionElemts.toString());
                        ///////////////////////////////
                    }
                    //motionAvgSpeed & pressure
                    motionAvgSpeed = sumSpeed / mVector.size();
                    motionAvgPressure = sumPressure / mVector.size();
                    //motionDuration & motionAbsLength
                    //Retrieving
                    mStructMotionElemts = (StructMotionElemts) mVector.firstElement();
                    firstPosX = mStructMotionElemts.getPosX();
                    firstPosY = mStructMotionElemts.getPosY();
                    startTime = mStructMotionElemts.getTime();
                    mStructMotionElemts = (StructMotionElemts) mVector.get(mVector.size() - 1);
                    lastPosX = mStructMotionElemts.getPosX();
                    lastPosY = mStructMotionElemts.getPosY();
                    endTime = mStructMotionElemts.getTime();
                    //Computing
                    motionDuration = endTime - startTime;
                    motionAbsLength = sqrt(pow((lastPosX - firstPosX), 2) + pow((lastPosY - firstPosY), 2));

                    //////Display///////////
                /*Log.v("TEST", "Absolute Length:" + motionAbsLength + "\n" +
                        "Total length: " + motionLength + "\n" +
                        "Duration :" + motionDuration + "\n" +
                        "Avg speed" + motionAvgSpeed + "\n" +
                        "\nEND OF ELEMENT\n");*/

                    mMotionInfo.setText("MOTION EVENT OVERALL VALUES\n" +
                            "Absolute Length : " + motionAbsLength + " px\n" +
                            "Total length : " + motionLength + " px\n" +
                            "Duration : " + motionDuration + " ms\n" +
                            "Avg speed : " + motionAvgSpeed + " px/s\n" +
                            "Avg pressure : " + motionAvgPressure);
                    ////////////////////////
                } else {
                    //Log.v("TEST", "\n Vecteur vide\n");
                }
                break;
        }
        return true;
    }
}

