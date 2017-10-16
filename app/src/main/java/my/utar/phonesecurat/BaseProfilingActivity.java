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

    private VelocityTracker mVelocityTracker = null;
    private TextView mMotionInfo = null;
    private TextView mSpeedDisplay = null;
    private ArrayList<StructMotionElemts> mList = null;
    private ArrayList<StructMotionFeatures> mModelList = null;
    private StructMotionElemts mStructMotionElemts = null;
    private StructMotionFeatures mStructMotionFeatures = null;
    private int rCounter = 0;
    private final static int NUMBER_OF_INTENT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        Intent i = getIntent();
        //TODO Assure le parcelable du List works, probably by making the structMotionFeatures parcelable AND structMotionElmts parcelabke
        mModelList = i.getParcelableExtra("mModelList");

        mSpeedDisplay = findViewById(R.id.speedDisplay);
        mMotionInfo = findViewById(R.id.motionInfo);

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
                }
                else{
                    mStructMotionFeatures.clear();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                mStructMotionElemts.compute(event, mList,mVelocityTracker);
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
                        "Avg pressure : " + mStructMotionFeatures.getMotionAvgPressure() );

                //if classifications ok

                //mModelList.addElement(mStructMotionFeatures);
/*                    rCounter = NUMBER_OF_INTENT;
                }
                else {
                    rCounter = NUMBER_OF_INTENT - mModelList.size();
                }*/
                break;
        }
        return true;
    }
}

