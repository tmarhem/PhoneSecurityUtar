package my.utar.phonesecurat;
/**
 * Learning phase Activity
 * Used to retrieve 10 right swipes them compute the model
 * After that the next swipes are compared to the model
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private TextView mTextCounterSwipeRight, mTextCounterSwipeLeft, mTextCounterScollUp, mTextCounterScrollDown;
    private ArrayList<StructMotionElemts> mPointsList;
    private ArrayList<StructMotionFeatures> mRightSwipeList, mLeftSwipeList, mScrollUpList, mScrollDownList;
    private UserModel mRightSwipeModel, mLeftSwipeModel, mScrollUpModel, mScrollDownModel;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private GestureDetector gestureDetector;
    private int counterSwipeRight, counterSwipeLeft, counterScrollUp, counterScrollDown;
    private final static int NUMBER_OF_INTENT = 10;
    private boolean mSwitch;
    Button mBtnReset;


    TextView mAbs;
    TextView length;
    TextView duration;
    TextView speed;
    TextView pressure;

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
        setContentView(R.layout.activity_base_profiling);
        mSwitch = false;

        mRightSwipeModel = new UserModel();
        mRightSwipeList = new ArrayList<>();
        counterSwipeRight = NUMBER_OF_INTENT - mRightSwipeList.size();
        mSpeedDisplay = findViewById(R.id.speedDisplay);
        mMotionInfo = findViewById(R.id.motionInfo);
        mTextCounterSwipeRight = findViewById(R.id.counterSwipeRight);
        mTextCounterSwipeRight.setText(Integer.toString(counterSwipeRight));
        mBtnReset = findViewById(R.id.btnReset);
        gestureDetector = new GestureDetector(mContext, new GestureListener());
        mAbs = findViewById(R.id.absLengthMatchResult);
        length = findViewById(R.id.lengthMatchResult);
        duration = findViewById(R.id.durationMatchResult);
        speed = findViewById(R.id.speedMatchResult);
        pressure = findViewById(R.id.pressureMatchResult);

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
                                mRightSwipeList.clear();
                                mRightSwipeModel.clear();
                                counterSwipeRight = 10;
                                mTextCounterSwipeRight.setText(Integer.toString(counterSwipeRight));
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
     * Display method
     */
    public void setPendingText(){
        mAbs.setText("   pending...");
        length.setText("    pending...");
        duration.setText("   pending...");
        speed.setText("   pending...");
        pressure.setText("   pending...");
    }

    /**
     *Extending the existing class SimpleOnGestureListener to our needs, customization of thresholds and recognized movements
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
         * @param e1 MotionEvent
         * @param e2 Motion Event
         * @param velocityX X axis instant Velocity
         * @param velocityY Y axis instant velocity
         * @return boolean true if movement considered onFling after checking thresholds
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

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY > 0)
                    onScrollUp();
                else
                    onScrollDown();
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
        return false;
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
                setPendingText();
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
                    //TODO DEBUG
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
    
    /**
     * If rmovement is recognized as right swipe, add movement to the list
     * When sample ma number is reached, model is computed
     * If model is already computed, goes to comparison
     */
    public void onSwipeRight() {
        if (counterSwipeRight >= 1) {
            mRightSwipeList.add(mStructMotionFeatures.clone());
            counterSwipeRight = NUMBER_OF_INTENT - mRightSwipeList.size();
            if (counterSwipeRight == 0 && mRightSwipeModel.getIsComputed()==0) {
                mRightSwipeModel.compute(mRightSwipeList);
            }
            mTextCounterSwipeRight.setText(String.format("%d", counterSwipeRight));
        }
        else {
            compare(mRightSwipeModel,mStructMotionFeatures);
        }
    }

    public void onSwipeLeft() {
        if (counterSwipeLeft >= 1) {
            mLeftSwipeList.add(mStructMotionFeatures.clone());
            counterSwipeLeft = NUMBER_OF_INTENT - mLeftSwipeList.size();
            if (counterSwipeLeft == 0 && mLeftSwipeModel.getIsComputed()==0) {
                mLeftSwipeModel.compute(mLeftSwipeList);
            }
            mTextCounterSwipeRight.setText(String.format("%d", counterSwipeLeft));
        }
        else {
            compare(mLeftSwipeModel,mStructMotionFeatures);
        }
    }
    
    public void onScrollUp() {
        if (counterScrollUp >= 1) {
            mScrollUpList.add(mStructMotionFeatures.clone());
            counterScrollUp = NUMBER_OF_INTENT - mScrollUpList.size();
            if (counterScrollUp == 0 && mScrollUpModel.getIsComputed()==0) {
                mScrollUpModel.compute(mScrollUpList);
            }
            mTextCounterSwipeRight.setText(String.format("%d", counterScrollUp));
        }
        else {
            compare(mScrollUpModel,mStructMotionFeatures);
        }
    }
    
    public void onScrollDown() {
        if (counterScrollDown >= 1) {
            mScrollDownList.add(mStructMotionFeatures.clone());
            counterScrollDown = NUMBER_OF_INTENT - mScrollDownList.size();
            if (counterScrollDown == 0 && mScrollDownModel.getIsComputed()==0) {
                mScrollDownModel.compute(mScrollDownList);
            }
            mTextCounterSwipeRight.setText(String.format("%d", counterScrollDown));
        }
        else {
            compare(mScrollDownModel,mStructMotionFeatures);
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

