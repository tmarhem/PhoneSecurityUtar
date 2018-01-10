package my.utar.phonesecurat;


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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.lang.Math;

/**
 * Learning phase Activity
 * Used to retrieve 10 right swipes them compute the model
 * After that the next swipes are compared to the model
 */
public class BaseProfilingActivity extends Activity {

    private final Context mContext = this;
    private VelocityTracker mVelocityTracker;
    private TextView mAvgValuesDisplay;
    private TextView mInstantValuesDisplay;
    private TextView mTextCounterSwipeRight, mTextCounterSwipeLeft, mTextCounterScollUp, mTextCounterScrollDown;
    private ArrayList<StructMotionElemts> mPointsList;
    private ArrayList<StructMotionFeatures> mSwipeRightList, mSwipeLeftList, mScrollUpList, mScrollDownList;
    private UserModel mSwipeRightModel, mSwipeLeftModel, mScrollUpModel, mScrollDownModel;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private GestureDetector gestureDetector;
    private int counterSwipeRight, counterSwipeLeft, counterScrollUp, counterScrollDown;
    private final static int NUMBER_OF_INTENT = 10;
    private boolean mSwitch, switchScrollUp, switchScrollDown, switchBlockSwipe;
    Button mBtnReset;
    Intent startIntent;

    TextView absLength;
    TextView length;
    TextView duration;
    TextView speed;
    TextView pressure;

    /**
     * Method launched on the creation of the activity
     * Retrieves and initialize everything that is needed
     *
     * @param savedInstanceState Bundle that saves information in case of sudden shutdown of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        mSwitch = false;
        switchScrollUp = false;
        switchScrollDown = false;
        switchBlockSwipe = false;

        mSwipeRightList = new ArrayList<>();
        mSwipeLeftList = new ArrayList<>();
        mScrollUpList = new ArrayList<>();
        mScrollDownList = new ArrayList<>();

        startIntent = this.getIntent();

        mSwipeRightModel = startIntent.getParcelableExtra("mSwipeRightModel");
        mSwipeLeftModel = startIntent.getParcelableExtra("mSwipeLeftModel");
        mScrollUpModel = startIntent.getParcelableExtra("mScrollUpModel");
        mScrollDownModel = startIntent.getParcelableExtra("mScrollDownModel");

        mInstantValuesDisplay = findViewById(R.id.instantValuesDisplay);
        mAvgValuesDisplay = findViewById(R.id.avgValuesDispay);
        absLength = findViewById(R.id.absLengthMatchResult);
        length = findViewById(R.id.lengthMatchResult);
        duration = findViewById(R.id.durationMatchResult);
        speed = findViewById(R.id.speedMatchResult);
        pressure = findViewById(R.id.pressureMatchResult);

        mTextCounterSwipeRight = findViewById(R.id.counterSwipeRight);
        mTextCounterSwipeLeft = findViewById(R.id.counterSwipeLeft);
        mTextCounterScollUp = findViewById(R.id.counterScrollUp);
        mTextCounterScrollDown = findViewById(R.id.counterScrollDown);

        mTextCounterSwipeRight.setText(Integer.toString(counterSwipeRight));
        mTextCounterSwipeLeft.setText(Integer.toString(counterSwipeLeft));
        mTextCounterScollUp.setText(Integer.toString(counterScrollUp));
        mTextCounterScrollDown.setText(Integer.toString(counterScrollDown));

        refreshCounters();

        mBtnReset = findViewById(R.id.btnReset);
        gestureDetector = new GestureDetector(mContext, new GestureListener());

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle("Warning");
                alertDialogBuilder
                        .setMessage("Are you sure you want to wipe the current saved model ?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSwipeRightList.clear();
                                mSwipeLeftList.clear();
                                mScrollUpList.clear();
                                mScrollDownList.clear();

                                mSwipeRightModel.clear();
                                mSwipeLeftModel.clear();
                                mScrollUpModel.clear();
                                mScrollDownModel.clear();

                                setPendingText();
                                refreshCounters();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent modelsFeedbackIntent = new Intent();
        modelsFeedbackIntent.putExtra("mSwipeRightModel", mSwipeRightModel);
        modelsFeedbackIntent.putExtra("mSwipeLeftModel", mSwipeLeftModel);
        modelsFeedbackIntent.putExtra("mScrollUpModel", mScrollUpModel);
        modelsFeedbackIntent.putExtra("mScrollDownModel", mScrollDownModel);

        setResult(RESULT_OK, modelsFeedbackIntent);
        super.onBackPressed();
    }

    /**
     * Display method
     */
    public void setPendingText() {
        absLength.setText("   pending...");
        length.setText("    pending...");
        duration.setText("   pending...");
        speed.setText("   pending...");
        pressure.setText("   pending...");
    }

    /**
     * Extending the existing class SimpleOnGestureListener to our needs, customization of thresholds and recognized movements
     */
    private class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 45;
        private static final int SWIPE_VELOCITY_THRESHOLD = 45;
        private static final int SCROLL_VELOCITY_THRESHOLD = 15;


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * Overriding existing method on SimpleOnGestureListener that triggers on a Swipe
         * Computing speed and direction to launch SwipeRight() or SwipeLeft()
         *
         * @param e1        MotionEvent
         * @param e2        Motion Event
         * @param velocityX X axis instant Velocity
         * @param velocityY Y axis instant velocity
         * @return boolean true if movement considered onFling after checking thresholds
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (!switchBlockSwipe) {
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distanceY = e2.getY() - e1.getY();

            if (Math.abs(velocityY) > SCROLL_VELOCITY_THRESHOLD) {
                if (distanceY < 0) {
                    switchScrollUp = true;
                } else if (distanceY > 0) {
                    switchScrollDown = true;
                }
                return true;
            }
            return false;
        }
    }


    /**
     * Method that triggers when you touch the screen
     *
     * @param event MotionEvent
     * @return boolean
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
/*
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
                mStructMotionElemts.compute(event, mPointsList, mVelocityTracker);
                //DISPLAY
                mInstantValuesDisplay.setText(mStructMotionElemts.toString());
                break;

            case MotionEvent.ACTION_UP:
                if (mSwitch) {
                    mStructMotionFeatures.compute(mPointsList);
                    mAvgValuesDisplay.setText(mStructMotionFeatures.toString());
                    mSwitch = false;
                    switchBlockSwipe = false;

                    float distanceY = mStructMotionFeatures.getLastPosY() - mStructMotionFeatures.getFirstPosY();
                    float distanceX = mStructMotionFeatures.getLastPosX() - mStructMotionFeatures.getFirstPosX();
                    double angle = Math.toDegrees(Math.atan(distanceY / distanceX));

                    if (switchScrollUp && !((angle > -50.0) && (angle < 50.0))) {
                        switchScrollUp = false;
                        switchBlockSwipe = true;
                        onScrollUp();
                    }


                    if (switchScrollDown && !((angle > -50.0) && (angle < 50.0))) {
                        switchScrollDown = false;
                        switchBlockSwipe = true;
                        onScrollDown();
                    }

                    switchScrollDown = false;
                    switchScrollUp = false;
                }
                break;
        }
        //Used at the end of a motionEvent move to trigger OnFling method
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * If movement is recognized as right swipe, add movement to the list
     * When sample max number is reached, model is computed
     * If model is already computed, goes to comparison
     */
    public void onSwipeRight() {
        if (counterSwipeRight >= 1) {
            mSwipeRightList.add(mStructMotionFeatures.clone());
            counterSwipeRight = NUMBER_OF_INTENT - mSwipeRightList.size();
            if (counterSwipeRight == 0 && mSwipeRightModel.getIsComputed() == 0) {
                mSwipeRightModel.compute(mSwipeRightList);
            }
            refreshCounters();
        } else {
            compare(mSwipeRightModel, mStructMotionFeatures);
        }
    }

    public void onSwipeLeft() {
        if (counterSwipeLeft >= 1) {
            mSwipeLeftList.add(mStructMotionFeatures.clone());
            counterSwipeLeft = NUMBER_OF_INTENT - mSwipeLeftList.size();
            if (counterSwipeLeft == 0 && mSwipeLeftModel.getIsComputed() == 0) {
                mSwipeLeftModel.compute(mSwipeLeftList);
            }
            refreshCounters();
        } else {
            compare(mSwipeLeftModel, mStructMotionFeatures);
        }
    }

    public void onScrollUp() {
        if (counterScrollUp >= 1) {
            mScrollUpList.add(mStructMotionFeatures.clone());
            counterScrollUp = NUMBER_OF_INTENT - mScrollUpList.size();
            if (counterScrollUp == 0 && mScrollUpModel.getIsComputed() == 0) {
                mScrollUpModel.compute(mScrollUpList);
            }
            refreshCounters();
        } else {
            compare(mScrollUpModel, mStructMotionFeatures);
        }
    }

    public void onScrollDown() {
        if (counterScrollDown >= 1) {
            mScrollDownList.add(mStructMotionFeatures.clone());
            counterScrollDown = NUMBER_OF_INTENT - mScrollDownList.size();
            if (counterScrollDown == 0 && mScrollDownModel.getIsComputed() == 0) {
                mScrollDownModel.compute(mScrollDownList);
            }
            refreshCounters();
        } else {
            compare(mScrollDownModel, mStructMotionFeatures);
        }
    }

    /**
     * Compares a move to the model
     *
     * @param mUserModel      UserModel
     * @param mStrangerMotion StructMotionsFeatures
     */
    public void compare(UserModel mUserModel, StructMotionFeatures mStrangerMotion) {

        TextView absLength = findViewById(R.id.absLengthMatchResult);
        TextView length = findViewById(R.id.lengthMatchResult);
        TextView duration = findViewById(R.id.durationMatchResult);
        TextView speed = findViewById(R.id.speedMatchResult);
        TextView pressure = findViewById(R.id.pressureMatchResult);

        absLength.setText("   not matched");
        length.setText("   not matched");
        duration.setText("   not matched");
        speed.setText("   not matched");
        pressure.setText("   not matched");

        double modelRatioLength = mUserModel.getAvgAbsLength() / mUserModel.getAvgLength();
        double strangerRatioLength = mStrangerMotion.getMotionAbsLength() / mStrangerMotion.getMotionLength();
        double ratioCurve = strangerRatioLength / modelRatioLength;

        double ratioLength = Long.valueOf(mUserModel.getAvgLength()).doubleValue() / Long.valueOf(mStrangerMotion.getMotionLength()).doubleValue();
        double ratioDuration = Long.valueOf(mUserModel.getAvgDuration()).doubleValue() / Long.valueOf(mStrangerMotion.getMotionDuration()).doubleValue();
        double ratioSpeed = mUserModel.getAvgSpeed() / mStrangerMotion.getMotionAvgSpeed();
        double ratioPressure = mUserModel.getAvgPressure() / mStrangerMotion.getMotionAvgPressure();


        absLength.setText("   " + toPercentage(ratioCurve) + " %");
        length.setText("   " + toPercentage(ratioLength) + " %");
        duration.setText("   " + toPercentage(ratioDuration) + " %");
        speed.setText("   " + toPercentage(ratioSpeed) + " %");
        pressure.setText("   " + toPercentage(ratioPressure) + " %");
    }

    public void refreshCounters() {
        if (mSwipeRightModel.getIsComputed() == 1) {
            counterSwipeRight = 0;
        } else {
            counterSwipeRight = NUMBER_OF_INTENT - mSwipeRightList.size();
        }

        if (mSwipeLeftModel.getIsComputed() == 1) {
            counterSwipeLeft = 0;
        } else {
            counterSwipeLeft = NUMBER_OF_INTENT - mSwipeLeftList.size();
        }

        if (mScrollUpModel.getIsComputed() == 1) {
            counterScrollUp = 0;
        } else {
            counterScrollUp = NUMBER_OF_INTENT - mScrollUpList.size();
        }

        if (mScrollDownModel.getIsComputed() == 1) {
            counterScrollDown = 0;
        } else {
            counterScrollDown = NUMBER_OF_INTENT - mScrollDownList.size();
        }

        mTextCounterSwipeRight.setText(Integer.toString(counterSwipeRight));
        mTextCounterSwipeLeft.setText(Integer.toString(counterSwipeLeft));
        mTextCounterScollUp.setText(Integer.toString(counterScrollUp));
        mTextCounterScrollDown.setText(Integer.toString(counterScrollDown));
    }

    public String toPercentage(Double ratio) {
        NumberFormat nf = new DecimalFormat("0.##");

        if (ratio > 1) {
            ratio = 1 / ratio;
        }
        ratio = ratio * 100;

        return nf.format(ratio);
    }
}

