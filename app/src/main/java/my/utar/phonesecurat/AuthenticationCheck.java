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

public class AuthenticationCheck extends IntentService {

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
        notifyUSer(Constants.TOAST.CREATION);

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


            // Window adding
            View mView = new HUDView(ctx);
            mSavedView = mView;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

            mParams.height = 1;
            mParams.width = 1;

            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            mParams.gravity = Gravity.END | Gravity.TOP;

            mWindowManager.addView(mView, mParams);
            gestureDetector = new GestureDetector(ctx, new GestureListener());


        } else if (intent.getAction().equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {
            this.onDestroy();
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
        stopForeground(true);
        mWindowManager.removeView(mSavedView);
        notifyUSer(Constants.TOAST.DESTRUCTION);
        super.onDestroy();
    }

    public void notifyUSer(int id) {
        switch (id){
            case Constants.TOAST.CREATION :
                Toast.makeText(ctx, "Service started", Toast.LENGTH_SHORT).show();
                break;
            case Constants.TOAST.DESTRUCTION :
                Toast.makeText(ctx, "Service stopped", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onSwipeRight() {
        Log.v("VERBOSE", "Entered onSwipeRight");

        //compare(mSwipeRightModel, mStructMotionFeatures);
    }

    public void onSwipeLeft() {
        Log.v("VERBOSE", "Entered onSwipeLeft");

        //compare(mSwipeLeftModel, mStructMotionFeatures);
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
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.v("VERBOSE", Integer.toString(event.getActionMasked()));
            performClick();
            return gestureDetector.onTouchEvent(event);
        }

        @Override
        public boolean performClick() {

            return super.performClick();
        }


    }
}
