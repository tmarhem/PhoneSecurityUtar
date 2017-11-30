package my.utar.phonesecurat;
/**
 * Principal Activity
 * Used to retrieve 10 right swipes them compute the model
 * After that the next swipes are compared to the model
 * TEST MASTER BRANCH
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

    /**
     * Method launched on the creation of the activity
     * @param savedInstanceState Bundle that saves information in case of sudden shutdown of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * Retrieving and initializing all what is necessary in the Activity
         */
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

    /**
     *Extending the existing class SimpleOnGestureListener to our need, customization of thresholds and recognized movements
     */
    private class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 66;
        private static final int SWIPE_VELOCITY_THRESHOLD = 66;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * Overriding existing method on SimpleOnGestureListener that triggers on a Swipe
         * Computing speed and direction to launch SwipeRight() or SwipeLeft()
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
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

    /**
     * Method not used, forced to implement it when implementing View.OnTouchListener in the Activity
     * @param v
     * @param event
     * @return
     */
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }



    /**
     * Method that triggers when you touch the screen
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
/**
 * ACTION_DOWN is the first touch of the screen
 * ACTION_UP is the last touch of the screen
 * ACTION_MOVE is all the intermediate points in between
 * We focus on ACTION_MOVE because on ACTION_DOWN / UP The speed can be 0 and mess the average
 */
            case MotionEvent.ACTION_DOWN:

                TextView mAbs = findViewById(R.id.absLengthMatchResult);
                TextView length = findViewById(R.id.lengthMatchResult);
                TextView duration = findViewById(R.id.durationMatchResult);
                TextView speed = findViewById(R.id.speedMatchResult);
                TextView pressure = findViewById(R.id.pressureMatchResult);
                mAbs.setText("   pending...");
                length.setText("    pending...");
                duration.setText("   pending...");
                speed.setText("   pending...");
                pressure.setText("   pending...");
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mSwitch) {
                    //INITIALISATION
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    //Creation or reinitialisation du List
                    if (mPointsList == null) {
                        mPointsList = new ArrayList<>();
                    } else {
                        mPointsList.clear();
                    }
                    //Creation or reinitialisation du StructMotionElemts
                    if (mStructMotionElemts == null) {
                        mStructMotionElemts = new StructMotionElemts();
                    } else {
                        mStructMotionElemts.clear();
                    }
                    //Creation or reinitialisation du StructMotionFeatures
                    if (mStructMotionFeatures == null) {
                        mStructMotionFeatures = new StructMotionFeatures();
                    } else {
                        mStructMotionFeatures.clear();
                    }
                    mSwitch = true;
                }
                if(mSwitch) {
                    mStructMotionElemts.compute(event, mPointsList, mVelocityTracker);
                    //DISPLAY
                    mSpeedDisplay.setText(mStructMotionElemts.toString());
                }
                break;

            case MotionEvent.ACTION_UP:
                if(mSwitch) {
                    mStructMotionFeatures.compute(mPointsList);
                    //DISPLAY
                    mMotionInfo.setText(mStructMotionFeatures.toString());
                    mSwitch = false;
                }
                break;
        }
        //Used at the end of a move to trigger OnFling method
        return gestureDetector.onTouchEvent(event);
    }

    public void onSwipeLeft() {
    }

    /**
     * If rmovement is recognized as right swipe, add movement to the list
     * When sample ma number is reached, model is computed
     * If model is already computed, goes to comparison
     */
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

    /**
     * Compares a move to the model
     * @param mUserModel
     * @param mStrangerMotion
     * @return
     */
    public boolean compare(UserModel mUserModel, StructMotionFeatures mStrangerMotion) {

        TextView mAbs = findViewById(R.id.absLengthMatchResult);
        TextView length = findViewById(R.id.lengthMatchResult);
        TextView duration = findViewById(R.id.durationMatchResult);
        TextView speed = findViewById(R.id.speedMatchResult);
        TextView pressure = findViewById(R.id.pressureMatchResult);




        Log.v("TEST","Entered cmp");
        boolean isAbsLengthMatched = false;
        boolean isLengthMatched = false;
        boolean isDurationMatched = false;
        boolean isSpeedMatched = false;
        boolean isPressureMatched = false;
        mAbs.setText("   not matched");
        length.setText("   not matched");
        duration.setText("   not matched");
        speed.setText("   not matched");
        pressure.setText("   not matched");




        double sensibility = 0.75;

        if (Math.abs(mUserModel.getAvgAbsLength() / mStrangerMotion.getMotionAbsLength()) >= sensibility) {
            isAbsLengthMatched = true;
            mAbs.setText("   MATCHED");
        }
        if (Math.abs((double) mUserModel.getAvgLength() / (double) mStrangerMotion.getMotionLength()) >= sensibility) {
            isLengthMatched = true;
            length.setText("   MATCHED");
        }

        if (Math.abs((double) mUserModel.getAvgDuration() / (double) mStrangerMotion.getMotionDuration()) >= sensibility) {
            isDurationMatched = true;
            duration.setText("   MATCHED");
        }

        if (Math.abs(mUserModel.getAvgSpeed() / mStrangerMotion.getMotionAvgSpeed()) >= sensibility) {
            isSpeedMatched = true;
            speed.setText("   MATCHED");
        }

        if (Math.abs(mUserModel.getAvgPressure() / mStrangerMotion.getMotionAvgPressure()) >= sensibility) {
            isPressureMatched = true;
            pressure.setText("   MATCHED");
        }

        return (isAbsLengthMatched && isLengthMatched && isDurationMatched && isSpeedMatched && isPressureMatched);
    }
}

