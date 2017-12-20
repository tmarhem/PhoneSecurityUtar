package my.utar.phonesecurat;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class AuthenticationCheck extends IntentService {

    private final Context ctx = this;
    private WindowManager mWindowManager;
    private GestureDetector gestureDetector;
    View mSavedView;
    private Handler mHandler;
    private boolean switchInitialize, switchScrollUp, switchScrollDown, switchBlockSwipe;
    private StructMotionFeatures mStructMotionFeatures;
    private VelocityTracker mVelocityTracker;
    private ArrayList<StructMotionElemts> mPointsList;
    private StructMotionElemts mStructMotionElemts;
    private boolean isRunning;


    public AuthenticationCheck() {
        super("AuthenticationCheck");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean sSR, sSL, sSD, sSU;
        UserModel mSwipeRightModel, mSwipeLeftModel, mScrollUpModel, mScrollDownModel;

        Log.v("TEST", "Service cree");
        notifyUSer(Constants.TOAST.CREATION);
        isRunning = false;
        switchInitialize = false;
        switchBlockSwipe = false;
        mHandler = new Handler();


        final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gsonLoad = new Gson();
        String mSRM = mPrefs.getString("mSwipeRightModel", "");
        String mSLM = mPrefs.getString("mSwipeLeftModel", "");
        String mSUM = mPrefs.getString("mScrollUpModel", "");
        String mSDM = mPrefs.getString("mScrollDownModel", "");
        mSwipeRightModel = gsonLoad.fromJson(mSRM, UserModel.class);
        mSwipeLeftModel = gsonLoad.fromJson(mSLM, UserModel.class);
        mScrollUpModel = gsonLoad.fromJson(mSUM, UserModel.class);
        mScrollDownModel = gsonLoad.fromJson(mSDM, UserModel.class);
        sSR = false;
        sSL = false;
        sSU = false;
        sSD = false;

        if (mSwipeRightModel != null) {
            sSR = true;
        }
        if (mSwipeLeftModel != null) {
            sSL = true;
        }
        if (mScrollUpModel != null) {
            sSU = true;
        }
        if (mScrollDownModel != null) {
            sSD = true;
        }

        Log.v("TEST", Boolean.toString(sSR) + Boolean.toString(sSL) + Boolean.toString(sSU) + Boolean.toString(sSD));

        //Foreground Service
        if (intent.getAction().equals(Constants.ACTION.START_FOREGROUND_ACTION)) {
            Intent notificationIntent = new Intent(ctx, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(ctx)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

            mHandler.postDelayed(addListeningWindow, 10000);


        } else if (intent.getAction().equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {
            this.onDestroy();
        }

        return START_STICKY;
    }

    private Runnable addListeningWindow = new Runnable() {
        public void run() {
            Log.v("VERBOSE", "Entered runnable");
            if (!isRunning) {
                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
            /*if(mSavedView != null) {
                mWindowManager.removeView(mSavedView);
            }*/
                // Window adding
                View mView = new HUDView(ctx);
                mSavedView = mView;
                mParams.height = 1;
                mParams.width = 1;
                mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                /*
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                */
                mParams.gravity = Gravity.END | Gravity.TOP;
                mWindowManager.addView(mView, mParams);
                gestureDetector = new GestureDetector(ctx, new GestureListener());
                mHandler.postDelayed(addListeningWindow, 10000);
                isRunning = true;
            }
            Log.v("VERBOSE", "Exiting runnable");
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public void onDestroy() {
        Log.v("TEST", "Service stopee");
        stopForeground(true);
        notifyUSer(Constants.TOAST.DESTRUCTION);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    public void notifyUSer(int id) {
        switch (id) {
            case Constants.TOAST.CREATION:
                Toast.makeText(ctx, "Service started", Toast.LENGTH_SHORT).show();
                break;
            case Constants.TOAST.DESTRUCTION:
                Toast.makeText(ctx, "Service stopped", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onSwipeRight() {
        Log.v("VERBOSE", "Entered onSwipeRight");

        //compare(mSwipeRightModel, mStructMotionFeatures);
        mWindowManager.removeView(mSavedView);
        isRunning = false;
        Log.v("VERBOSE", "View closed");

    }

    public void onSwipeLeft() {
        Log.v("VERBOSE", "Entered onSwipeLeft");

        //compare(mSwipeLeftModel, mStructMotionFeatures);
        mWindowManager.removeView(mSavedView);
        isRunning = false;
        Log.v("VERBOSE", "View closed");

    }

    public void onScrollUp() {
        //compare(mScrollUpModel, mStructMotionFeatures);
        mWindowManager.removeView(mSavedView);
        isRunning = false;

    }

    public void onScrollDown() {
        //compare(mScrollDownModel, mStructMotionFeatures);
        mWindowManager.removeView(mSavedView);
        isRunning = false;

    }

    public void compare(UserModel mUserModel, StructMotionFeatures mStrangerMotion) {
        /*boolean isAbsLengthMatched = false;
        boolean isLengthMatched = false;
        boolean isDurationMatched = false;
        boolean isSpeedMatched = false;
        boolean isPressureMatched = false;*/
        double sensibility = 0.75;

        if (Math.abs(mUserModel.getAvgAbsLength() / mStrangerMotion.getMotionAbsLength()) >= sensibility) {
            //isAbsLengthMatched = true;
            Log.v("RESULT", "absLength matched");
        }
        if (Math.abs((double) mUserModel.getAvgLength() / (double) mStrangerMotion.getMotionLength()) >= sensibility) {
            //isLengthMatched = true;
            Log.v("RESULT", "length matched");
        }

        if (Math.abs((double) mUserModel.getAvgDuration() / (double) mStrangerMotion.getMotionDuration()) >= sensibility) {
            //isDurationMatched = true;
            Log.v("RESULT", "duration matched");
        }

        if (Math.abs(mUserModel.getAvgSpeed() / mStrangerMotion.getMotionAvgSpeed()) >= sensibility) {
            //isSpeedMatched = true;
            Log.v("RESULT", "duration matched");
        }

        if (Math.abs(mUserModel.getAvgPressure() / mStrangerMotion.getMotionAvgPressure()) >= sensibility) {
            //isPressureMatched = true;
            Log.v("RESULT", "pressure matched");
        }
    }

    /**
     * Extending the existing class SimpleOnGestureListener to our needs, customization of thresholds and recognized movements
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 66;
        private static final int SWIPE_VELOCITY_THRESHOLD = 66;
        private static final int SCROLL_VELOCITY_THRESHOLD = 35;


        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        /**
         * Overriding existing method on SimpleOnGestureListener that triggers on a Swipe
         * Computing speed and direction to launch SwipeRight() or SwipeLeft()
         *
         * @param e1        MotionEvent
         * @param e2        Motion Event
         * @param velocityX X axis instant Velocity
         * @param velocityY Y axis instant velocity
         * @return boolean false for not consuming the event
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
            }
            return false;
        }
    }

    class HUDView extends ViewGroup implements View.OnTouchListener {

        public HUDView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            Log.d(this.getClass().getName(), "onkeydown");

            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                Log.d(this.getClass().getName(), "back button pressed");
            }
            return super.onKeyDown(keyCode, event);
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
/*
 * ACTION_DOWN is the first touch of the screen
 * ACTION_UP is the last touch of the screen
 * ACTION_MOVE is all the intermediate points in between
 * We focus on ACTION_MOVE because on ACTION_DOWN / UP The speed can be 0 and mess the average
 */
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!switchInitialize) {
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
                        switchInitialize = true;
                    }
                    mStructMotionElemts.compute(event, mPointsList, mVelocityTracker);
                    //DISPLAY
                    break;

                case MotionEvent.ACTION_UP:
                    if (switchInitialize) {
                        mStructMotionFeatures.compute(mPointsList);
                        switchInitialize = false;
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
                    mWindowManager.removeView(mSavedView);
                    isRunning = false;
                    break;
            }
            performClick();
            return gestureDetector.onTouchEvent(event);
        }

        @Override
        public boolean performClick() {

            return super.performClick();
        }


    }
}
