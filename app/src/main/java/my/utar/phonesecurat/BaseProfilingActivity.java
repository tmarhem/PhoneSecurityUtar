package my.utar.phonesecurat;
/*
Activity for model feature extraction
//TODO Size not working ?
//TODO ADDITIONNAL Draw the swipe
//TODO Swipe, scroll and touch classification
//TODO Left and Right recognition
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BaseProfilingActivity extends Activity {

    private VelocityTracker mVelocityTracker;
    private TextView mMotionInfo;
    private TextView mSpeedDisplay;
    private TextView mTextCounter;
    private ArrayList<StructMotionElemts> mList;
    private StructMotionFeaturesList mModelList;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private int rCounter;
    private final static int NUMBER_OF_INTENT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        Intent i = getIntent();
        //TODO Assure le parcelable du List works, probably by making the structMotionFeatures parcelable AND structMotionElmts parcelabke
        Bundle b = getIntent().getExtras();
        mModelList = b.getParcelable("mModelList");
        rCounter = NUMBER_OF_INTENT - mModelList.size();
        mSpeedDisplay = findViewById(R.id.speedDisplay);
        mMotionInfo = findViewById(R.id.motionInfo);
        mTextCounter = findViewById(R.id.textCounter);
        mTextCounter.setText(Integer.toString(rCounter));
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //Creation ou reinitialisation du VelocityTracker
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
                break;

            case MotionEvent.ACTION_MOVE:
                mStructMotionElemts.compute(event, mList, mVelocityTracker);
                mSpeedDisplay.setText(mStructMotionElemts.toString());
                break;

            //Exiting event, going though the List by its iterator and making calulations
            case MotionEvent.ACTION_UP:
                mStructMotionFeatures.compute(mList);
                mMotionInfo.setText("MOTION EVENT OVERALL VALUES\n" +
                        "Absolute Length : " + mStructMotionFeatures.getMotionAbsLength() + " px\n" +
                        "Total length : " + mStructMotionFeatures.getMotionLength() + " px\n" +
                        "Duration : " + mStructMotionFeatures.getMotionDuration() + " ms\n" +
                        "Avg speed : " + mStructMotionFeatures.getMotionAvgSpeed() + " px/s\n" +
                        "Avg pressure : " + mStructMotionFeatures.getMotionAvgPressure());


                if (rCounter >= 1) {

                    mModelList.add(mStructMotionFeatures);
                    rCounter = NUMBER_OF_INTENT - mModelList.size();
                    mTextCounter.setText(Integer.toString(rCounter));
                } else {
                    //FINISH, send back the list and close
                }

                break;
        }
        return true;
    }
}

