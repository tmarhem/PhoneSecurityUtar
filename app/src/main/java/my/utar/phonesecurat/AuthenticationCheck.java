package my.utar.phonesecurat;

import android.app.ActionBar;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AuthenticationCheck extends IntentService implements View.OnTouchListener {

    private WindowManager mWindowManager;
    private LinearLayout mLinearLayout;

    private GestureDetector gestureDetector;
    private GestureListener gestureListener;
    private VelocityTracker mVelocityTracker;
    private ArrayList<StructMotionElemts> mPointsList;
    private StructMotionFeaturesList mModelList;
    private UserModel mRightSwipeModel;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private boolean mSwitch;


    public AuthenticationCheck() {
        super("AuthenticationCheck");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"Authentication service started", Toast.LENGTH_SHORT).show();
        Log.v("TEST","LOG CHECK");

        /*ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        );*/

        mLinearLayout = new LinearLayout(getApplicationContext());
        LayoutParams mLayoutParams = new LayoutParams(1,1);
        mLinearLayout.setLayoutParams(mLayoutParams);
        mLinearLayout.setOnTouchListener(this);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                1,
                1,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        );
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(mLinearLayout, mParams);

        gestureListener = new GestureListener() {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }
        };
        mRightSwipeModel = new UserModel();
        //TODO retrieve UserModel saved
        mSwitch = false;

        gestureDetector = new GestureDetector(getApplicationContext(), gestureListener);
        Log.v("TEST","GESTURE DETECTOR CREATED");




        return START_REDELIVER_INTENT;
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.v("TEST","ENTERED ONTOUCH");
        return gestureDetector.onTouchEvent(event);
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
                }
                break;

            case MotionEvent.ACTION_UP:
                if(mSwitch) {
                    mStructMotionFeatures.compute(mPointsList);
                    //DISPLAY
                    mSwitch = false;
                }
                break;
        }
        //Used at the end of a move to trigger OnFling method
        return gestureDetector.onTouchEvent(event);
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
        if(mWindowManager != null) {
            if(mLinearLayout != null) mWindowManager.removeView(mLinearLayout);
        }
        Toast.makeText(this,"Authentication service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
