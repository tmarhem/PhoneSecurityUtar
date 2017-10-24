package my.utar.phonesecurat;
/*
Activity for model feature extraction
//TODO Size not working ?
//TODO ADDITIONNAL Draw the swipe
//TODO Swipe, scroll and touch classification
//TODO Left and Right recognition
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BaseProfilingActivity extends Activity {

    private final Context mContext = this;
    private VelocityTracker mVelocityTracker;
    private TextView mMotionInfo;
    private TextView mSpeedDisplay;
    private TextView mTextCounter;
    private Button mBtnReset;
    private ArrayList<StructMotionElemts> mList;
    private StructMotionFeaturesList mModelList;
    private UserModel mRightSwipeModel;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private int rCounter;
    private final static int NUMBER_OF_INTENT = 10;
    private boolean mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        mSwitch = false;
        Intent i = getIntent();
        Bundle b = getIntent().getExtras();
        mRightSwipeModel = b.getParcelable("mRightSwipeModel");
        mModelList = new StructMotionFeaturesList();
        rCounter = NUMBER_OF_INTENT - mModelList.size();
        mSpeedDisplay = findViewById(R.id.speedDisplay);
        mMotionInfo = findViewById(R.id.motionInfo);
        mTextCounter = findViewById(R.id.textCounter);
        mTextCounter.setText(Integer.toString(rCounter));
        mBtnReset = findViewById(R.id.btnReset);

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
                                rCounter = 10;
                                mModelList.clear();
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

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:

                if (mSwitch == false) {

                    //INITIALISATION
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    //Creation ou reinitialisation du List
                    if (mList == null) {
                        mList = new ArrayList<>();
                    } else {
                        mList.clear();
                    }
                    //Creation ou reinitialisation du StructMotionElemts
                    if (mStructMotionElemts == null) {
                        mStructMotionElemts = new StructMotionElemts();
                    } else {
                        mStructMotionElemts.clear();
                    }
                    if (mStructMotionFeatures == null) {
                        mStructMotionFeatures = new StructMotionFeatures();
                    } else {
                        mStructMotionFeatures.clear();
                    }

                    mSwitch = true;

                }

                mStructMotionElemts.compute(event, mList, mVelocityTracker);
                mSpeedDisplay.setText(mStructMotionElemts.toString());
                Log.v("TEST", Integer.toString(event.getHistorySize()));

                break;

            case MotionEvent.ACTION_UP:
                mStructMotionFeatures.compute(mList);
                mMotionInfo.setText("MOTION EVENT OVERALL VALUES\n" +
                        "Absolute Length : " + mStructMotionFeatures.getMotionAbsLength() + " px\n" +
                        "Total length : " + mStructMotionFeatures.getMotionLength() + " px\n" +
                        "Duration : " + mStructMotionFeatures.getMotionDuration() + " ms\n" +
                        "Avg speed : " + mStructMotionFeatures.getMotionAvgSpeed() + " px/s\n" +
                        "Avg pressure : " + mStructMotionFeatures.getMotionAvgPressure());
                mSwitch = true;

                if (rCounter >= 1) {

                    mModelList.add(mStructMotionFeatures);
                    rCounter = NUMBER_OF_INTENT - mModelList.size();
                    mTextCounter.setText(Integer.toString(rCounter));
                } else {
                    //COMPARE TO MODEL

                }

                break;
        }
        return true;
    }
}

