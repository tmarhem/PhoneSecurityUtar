package my.utar.phonesecurat;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.data;

public class AuthenticationCheck extends IntentService implements View.OnTouchListener {

    private  final Context ctx = this;
    private WindowManager mWindowManager;
    private GestureDetector gestureDetector;
    private GestureListener gestureListener;
    View mSavedView;

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
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Truiton Music Player")
                    .setTicker("Truiton Music Player")
                    .setContentText("My Music")
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);


            // Window adding and listener
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                    700,
                    700,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
/*
                PixelFormat.TRANSLUCENT
*/
            );
            mParams.gravity = Gravity.START | Gravity.TOP;
            View mView = LayoutInflater.from(AuthenticationCheck.this).inflate(R.layout.floating_layout,null);
            mView.setOnTouchListener(AuthenticationCheck.this);
            mSavedView = mView;
            mWindowManager.addView(mView, mParams);
            mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.v("TEST","ENTERED ON TOUCH");
                    return false;

                }


            });
            gestureListener = new GestureListener() {
                @Override
                public void onSwipeRight() {
                    Log.v("TEST","ENTERED ON SWIPE RIGHT");
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    Log.v("TEST","ENTERED ON DOWN");
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    Log.v("TEST","ENTERED ON SCROLL");
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }
            };
            gestureDetector = new GestureDetector(this, gestureListener);

        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i("VERBOSE", "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }


        return START_STICKY;
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.v("TEST","ENTERED ONTOUCH");
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.v("TEST","ENTERED ONTOUCHEVENT");
        switch (event.getActionMasked()) {
/**
 * ACTION_DOWN is the first touch of the screen
 * ACTION_UP is the last touch of the screen
 * ACTION_MOVE is all the intermediate points in between
 * We focus on ACTION_MOVE because on ACTION_DOWN / UP The speed can be 0 and mess the average
 */
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                Log.v("TEST","ACTION MOVE");
                break;
        }
        //Used at the end of a move to trigger OnFling method
        return false;
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

    public void notifyOnDestroy(){
        Log.v("VERBOSE","Service destroyed");
    }

    public void removeListeningView(){
        mWindowManager.removeView(mSavedView);
    }

}
