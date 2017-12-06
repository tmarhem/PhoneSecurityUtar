package my.utar.phonesecurat;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class AuthenticationCheck extends IntentService{

    private final Context ctx = this;
    private WindowManager mWindowManager;
    private GestureDetector gestureDetector;
    View mSavedView;
    private boolean mSwitch, switchScrollUp, switchScrollDown, switchBlockSwipe;
    private UserModel mSwipeRightModel, mSwipeLeftModel, mScrollUpModel, mScrollDownModel;
    private StructMotionElemts mStructMotionElmts;
    private StructMotionFeatures mStructMotionFeatures;


    public AuthenticationCheck() {
        super("AuthenticationCheck");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("VERBOSE", "onStartCommand");

        //Foreground Service
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i("VERBOSE", "Received Start Foreground Intent ");
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


            // Window adding and listener
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                    700,
                    700,
                    //WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    //WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FIRST_SUB_WINDOW,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            );
            mParams.gravity = Gravity.END | Gravity.TOP;
            //View mView = LayoutInflater.from(AuthenticationCheck.this).inflate(R.layout.floating_layout, null);
            View mView = new HUDView(ctx);
            mSavedView = mView;
            mWindowManager.addView(mView, mParams);


            //Listener
            gestureDetector = new GestureDetector(ctx, new GestureListener());


            mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i("VERBOSE", "ENTERED ONTOucH");
                    gestureDetector.onTouchEvent(event);
                    return false;
                }});

        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        }


        return START_STICKY;
    }

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
        mWindowManager.removeView(mSavedView);
        notifyOnDestroy();
        super.onDestroy();
    }

    public void notifyOnDestroy() {
        Log.v("VERBOSE", "Service destroyed");
    }

    public void onSwipeRight() {
        compare(mSwipeRightModel, mStructMotionFeatures);
    }

    public void onSwipeLeft() {
        compare(mSwipeLeftModel, mStructMotionFeatures);
    }

    public void onScrollUp() {
        compare(mScrollUpModel, mStructMotionFeatures);
    }

    public void onScrollDown() {
        compare(mScrollDownModel, mStructMotionFeatures);
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

    class HUDView extends ViewGroup implements View.OnTouchListener{

        public HUDView(Context context) {
            super(context);
            Toast.makeText(getContext(), "HUDView", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.v("VERBOSE", "Entered OnTouch");
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.v("VERBOSE", "Entered OnTouchEvent");
            performClick();
            return true;
        }

        @Override
        public boolean performClick(){
           return super.performClick();
        }



    }
}
