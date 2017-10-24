package my.utar.phonesecurat;
/*
Activity for model feature extraction
//TODO Size not working ?
//TODO ADDITIONNAL Draw the swipe
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
    private ArrayList<StructMotionElemts> mPointsList;
    private StructMotionFeaturesList mModelList;
    private UserModel mRightSwipeModel;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private GestureDetector gestureDetector;
    private int rCounter;
    private final static int NUMBER_OF_INTENT = 10;
    private boolean mSwitch;

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends SimpleOnGestureListener {

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
        //mRightSwipeModel = b.getParcelable("mRightSwipeModel");
        mRightSwipeModel = new UserModel();
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
                                mModelList.clear();
                                mRightSwipeModel.clear();
                                rCounter = 10;
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
                if (!mSwitch) {
                    //INITIALISATION
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    //Creation ou reinitialisation du List
                    if (mPointsList == null) {
                        mPointsList = new ArrayList<>();
                    } else {
                        mPointsList.clear();
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
                mStructMotionElemts.compute(event, mPointsList, mVelocityTracker);
                mSpeedDisplay.setText(mStructMotionElemts.toString());
                break;

            case MotionEvent.ACTION_UP:
                mStructMotionFeatures.compute(mPointsList);
                mMotionInfo.setText(mStructMotionFeatures.toString());
                mSwitch = false;
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {

        if (rCounter >= 1) {

            mModelList.add(mStructMotionFeatures.clone());
            rCounter = NUMBER_OF_INTENT - mModelList.size();
            if (rCounter == 0 && mRightSwipeModel.getIsComputed()==0) {
                mRightSwipeModel.compute(mModelList);
            }
            mTextCounter.setText(String.format("%d", rCounter));

        }
        else {
            compare(mRightSwipeModel,mStructMotionFeatures);
        }

    }

    public boolean compare(UserModel mUserModel, StructMotionFeatures mStrangerMotion) {

        Log.v("TEST","Entered cmp");
        boolean isAbsLengthMatched = false;
        boolean isLengthMatched = false;
        boolean isDurationMatched = false;
        boolean isSpeedMatched = false;
        boolean isPressureMatched = false;
        double sensibility = 0.75;

        if (Math.abs(mUserModel.getAvgAbsLength() / mStrangerMotion.getMotionAbsLength()) >= sensibility) {
            isAbsLengthMatched = true;
        }
        if (Math.abs((double) mUserModel.getAvgLength() / (double) mStrangerMotion.getMotionLength()) >= sensibility) {
            isLengthMatched = true;
        }

        if (Math.abs((double) mUserModel.getAvgDuration() / (double) mStrangerMotion.getMotionDuration()) >= sensibility) {
            isDurationMatched = true;
        }

        if (Math.abs(mUserModel.getAvgSpeed() / mStrangerMotion.getMotionAvgSpeed()) >= sensibility) {
            isSpeedMatched = true;
        }

        if (Math.abs(mUserModel.getAvgPressure() / mStrangerMotion.getMotionAvgPressure()) >= sensibility) {
            isPressureMatched = true;
        }

        return (isAbsLengthMatched && isLengthMatched && isDurationMatched && isSpeedMatched && isPressureMatched);
    }
}

