package my.utar.phonesecurat;
/*
Activity for model feature extraction
//TODO Size not working ?
//TODO ADDITIONNAL Draw the swipe
//TODO Swipe, scroll and touch classification
//TODO Left and Right recognition
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BaseProfilingActivity extends Activity implements View.OnTouchListener {

    private final Context mContext = this;
    private VelocityTracker mVelocityTracker;
    private TextView mMotionInfo;
    private TextView mSpeedDisplay;
    private TextView mTextCounter;
    private ArrayList<StructMotionElemts> mList;
    private StructMotionFeaturesList mModelList;
    private UserModel mRightSwipeModel;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private GestureDetector gestureDetector;
    private int rCounter;
    private final static int NUMBER_OF_INTENT = 10;
    private boolean mSwitch;

    public boolean compare(UserModel mUserModel, UserModel mStrangerModel){
        boolean isAbsLengthMatched = false;
        boolean isLengthMatched = false;
        boolean isDurationMatched = false;
        boolean isSpeedMatched = false;
        boolean isPressureMatched = false;
        double sensibility = 0.75;

        if(Math.abs(mUserModel.getAvgAbsLength()/mStrangerModel.getAvgAbsLength())>sensibility){
            isAbsLengthMatched = true;
        }

        return (isAbsLengthMatched && isLengthMatched && isDurationMatched && isSpeedMatched && isPressureMatched);
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {

        if (rCounter >= 1) {

            mModelList.add(mStructMotionFeatures);
            rCounter = NUMBER_OF_INTENT - mModelList.size();
            mTextCounter.setText(String.format("%d", rCounter));

        } else if (rCounter == 0) {
            mRightSwipeModel.compute(mModelList);
        }
        else{
            //compare(mRightSwipe
        }

    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        setContentView(R.layout.activity_base_profiling);
        mSwitch = false;
        Intent i = getIntent();
        Bundle b = getIntent().getExtras();
        mRightSwipeModel = b.getParcelable("mRightSwipeModel");
        mModelList = new StructMotionFeaturesList();
        rCounter = NUMBER_OF_INTENT - mModelList.size();
        mSpeedDisplay = findViewById(R.id.speedDisplay);
        mMotionInfo = findViewById(R.id.motionInfo);
        mTextCounter = findViewById(R.id.textCounter);
        mTextCounter.setText(Integer.toString(rCounter));
        Button mBtnReset;
        mBtnReset = findViewById(R.id.btnReset);

        gestureDetector = new GestureDetector(context, new GestureListener());


        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                // set title
                alertDialogBuilder.setTitle("Warning");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to wipe the current saved model ?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                rCounter = 10;
                                mModelList.clear();
                                mTextCounter.setText(Integer.toString(rCounter));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:

                if (mSwitch == false) {

                    //INITIALISATION
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    //Creation ou reinitialisation du List
                    if (mList == null) {
                        mList = new ArrayList<>();
                    } else {
                        mList.clear();
                    }
                    //Creation ou reinitialisation du StructMotionElemts
                    if (mStructMotionElemts == null) {
                        mStructMotionElemts = new StructMotionElemts();
                    } else {
                        mStructMotionElemts.clear();
                    }
                    if (mStructMotionFeatures == null) {
                        mStructMotionFeatures = new StructMotionFeatures();
                    } else {
                        mStructMotionFeatures.clear();
                    }

                    mSwitch = true;

                }

                mStructMotionElemts.compute(event, mList, mVelocityTracker);
                mSpeedDisplay.setText(mStructMotionElemts.toString());

                break;

            case MotionEvent.ACTION_UP:
                mStructMotionFeatures.compute(mList);
                mMotionInfo.setText("MOTION EVENT OVERALL VALUES\n" +
                        "Absolute Length : " + mStructMotionFeatures.getMotionAbsLength() + " px\n" +
                        "Total length : " + mStructMotionFeatures.getMotionLength() + " px\n" +
                        "Duration : " + mStructMotionFeatures.getMotionDuration() + " ms\n" +
                        "Avg speed : " + mStructMotionFeatures.getMotionAvgSpeed() + " px/s\n" +
                        "Avg pressure : " + mStructMotionFeatures.getMotionAvgPressure());
                mSwitch = true;

                break;
        }
        return gestureDetector.onTouchEvent(event);
    }
}

