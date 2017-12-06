package my.utar.phonesecurat;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
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
    private TextView mIstantValuesDisplay;
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

        final SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String mSRM = mPrefs.getString("mSwipeRightModel","");
        String mSLM = mPrefs.getString("mSwipeLeftModel", "");
        String mSUM = mPrefs.getString("mScrollUpModel", "");
        String mSDM = mPrefs.getString("mScrollDownModel", "");
        mSwipeRightModel = gsonLoad.fromJson(mSRM, UserModel.class);
        mSwipeLeftModel = gsonLoad.fromJson(mSLM, UserModel.class);
        mScrollUpModel = gsonLoad.fromJson(mSUM, UserModel.class);
        mScrollDownModel = gsonLoad.fromJson(mSDM, UserModel.class);


        if(mSwipeRightModel != null){
            counterSwipeRight = 0;

        }
        else{
            mSwipeRightModel = new UserModel();
            counterSwipeRight = NUMBER_OF_INTENT - mSwipeRightList.size();
        }

        if(mSwipeLeftModel != null){
            counterSwipeLeft = 0;
        }
        else{
            mSwipeLeftModel = new UserModel();
            counterSwipeLeft = NUMBER_OF_INTENT - mSwipeLeftList.size();
        }

        if(mScrollUpModel != null){
            counterScrollUp = 0;
        }
        else{
            mScrollUpModel = new UserModel();
            counterScrollUp = NUMBER_OF_INTENT - mScrollUpList.size();
        }

        if(mScrollDownModel != null){
            counterScrollDown = 0;
        }
        else{
            mScrollDownModel = new UserModel();
            counterScrollDown = NUMBER_OF_INTENT - mScrollDownList.size();
        }

        mSwipeLeftModel = new UserModel();
        mScrollUpModel = new UserModel();
        mScrollDownModel = new UserModel();

        mIstantValuesDisplay = findViewById(R.id.instantValuesDisplay);
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

                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.clear();
                                prefsEditor.apply();


                                counterSwipeRight = NUMBER_OF_INTENT - mSwipeRightList.size();
                                counterSwipeLeft = NUMBER_OF_INTENT - mSwipeLeftList.size();
                                counterScrollUp = NUMBER_OF_INTENT - mScrollUpList.size();
                                counterScrollDown = NUMBER_OF_INTENT - mScrollDownList.size();

                                mTextCounterSwipeRight.setText(Integer.toString(counterSwipeRight));
                                mTextCounterSwipeLeft.setText(Integer.toString(counterSwipeLeft));
                                mTextCounterScollUp.setText(Integer.toString(counterScrollUp));
                                mTextCounterScrollDown.setText(Integer.toString(counterScrollDown));

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

        private static final int SWIPE_DISTANCE_THRESHOLD = 66;
        private static final int SWIPE_VELOCITY_THRESHOLD = 66;
        private static final int SCROLL_VELOCITY_THRESHOLD = 35;


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
            if(!switchBlockSwipe) {
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
                    mIstantValuesDisplay.setText(mStructMotionElemts.toString());
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
            if (counterSwipeRight == 0 && mSwipeRightModel.getIsComputed()==0) {
                mSwipeRightModel.compute(mSwipeRightList);
                SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gsonSave = new Gson();
                String json = gsonSave.toJson(mSwipeRightModel);
                prefsEditor.putString("mSwipeRightModel", json);
                prefsEditor.apply();
            }
            mTextCounterSwipeRight.setText(String.format("%d", counterSwipeRight));
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
                SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gsonSave = new Gson();
                String json = gsonSave.toJson(mSwipeLeftModel);
                prefsEditor.putString("mSwipeLeftModel", json);
                prefsEditor.apply();
            }
            mTextCounterSwipeLeft.setText(String.format("%d", counterSwipeLeft));
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
                SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gsonSave = new Gson();
                String json = gsonSave.toJson(mScrollUpModel);
                prefsEditor.putString("mScrollUpModel", json);
                prefsEditor.apply();
            }
            mTextCounterScollUp.setText(String.format("%d", counterScrollUp));
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
                SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gsonSave = new Gson();
                String json = gsonSave.toJson(mScrollDownModel);
                prefsEditor.putString("mScrollDownModel", json);
                prefsEditor.apply();
            }
            mTextCounterScrollDown.setText(String.format("%d", counterScrollDown));
        } else {
            compare(mScrollDownModel, mStructMotionFeatures);
        }
    }

    /**
     * Compares a move to the model
     *
     * @param mUserModel UserModel
     * @param mStrangerMotion StructMotionsFeatures
     */
    public void compare(UserModel mUserModel, StructMotionFeatures mStrangerMotion) {

        TextView absLength = findViewById(R.id.absLengthMatchResult);
        TextView length = findViewById(R.id.lengthMatchResult);
        TextView duration = findViewById(R.id.durationMatchResult);
        TextView speed = findViewById(R.id.speedMatchResult);
        TextView pressure = findViewById(R.id.pressureMatchResult);

        /*boolean isAbsLengthMatched = false;
        boolean isLengthMatched = false;
        boolean isDurationMatched = false;
        boolean isSpeedMatched = false;
        boolean isPressureMatched = false;*/
        absLength.setText("   not matched");
        length.setText("   not matched");
        duration.setText("   not matched");
        speed.setText("   not matched");
        pressure.setText("   not matched");

        double sensibility = 0.75;

        if (Math.abs(mUserModel.getAvgAbsLength() / mStrangerMotion.getMotionAbsLength()) >= sensibility) {
            //isAbsLengthMatched = true;
            absLength.setText("   MATCHED");
        }
        if (Math.abs((double) mUserModel.getAvgLength() / (double) mStrangerMotion.getMotionLength()) >= sensibility) {
            //isLengthMatched = true;
            length.setText("   MATCHED");
        }

        if (Math.abs((double) mUserModel.getAvgDuration() / (double) mStrangerMotion.getMotionDuration()) >= sensibility) {
            //isDurationMatched = true;
            duration.setText("   MATCHED");
        }

        if (Math.abs(mUserModel.getAvgSpeed() / mStrangerMotion.getMotionAvgSpeed()) >= sensibility) {
            //isSpeedMatched = true;
            speed.setText("   MATCHED");
        }

        if (Math.abs(mUserModel.getAvgPressure() / mStrangerMotion.getMotionAvgPressure()) >= sensibility) {
            //isPressureMatched = true;
            pressure.setText("   MATCHED");
        }

    }
}

