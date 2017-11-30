package my.utar.phonesecurat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
    private LinearLayout mLinearLayout;

    private GestureDetector gestureDetector;
    private GestureListener gestureListener;
    private VelocityTracker mVelocityTracker;
    private ArrayList<StructMotionElemts> mPointsList;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private boolean mSwitch;

View mSavedView;

    public AuthenticationCheck() {
        super("AuthenticationCheck");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(getApplicationContext(),"Authentication service started", Toast.LENGTH_SHORT).show();
        Log.v("TEST","LOG CHECK++");

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                700,
                700,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                //PixelFormat.TRANSLUCENT
        );
        mParams.gravity = Gravity.START | Gravity.TOP;
///////////////////////////////////////////////////////////////
        View mView = LayoutInflater.from(AuthenticationCheck.this).inflate(R.layout.floating_layout,null);
        mView.setOnTouchListener(AuthenticationCheck.this);
        mSavedView = mView;
        mWindowManager.addView(mView, mParams);


        Log.v("TEST","LOG 4");

        gestureListener = new GestureListener() {
            @Override
            public void onSwipeRight() {
                Log.v("TEST","ENTERED ON SWIPE RIGHT");
                ;
            }
        };
        mSwitch = false;

        gestureDetector = new GestureDetector(ctx, gestureListener);
        Log.v("TEST","GESTURE DETECTOR CREATED");


        return START_REDELIVER_INTENT;
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
        /*if(mWindowManager != null) {
            if(mView != null) mWindowManager.removeView(mView);
        }*/
        Toast.makeText(this,"Authentication service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        //mWindowManager.removeView(mSavedView);

    }
}
