package my.utar.phonesecurat;
/**
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
import java.util.Vector;


public class BaseProfilingActivity extends Activity {

    private VelocityTracker mVelocityTracker = null;
    private TextView mMotionInfo = null;
    private TextView mSpeedDisplay = null;
    private Vector mVector = null;
    private StructMotionElemts mStructMotionElemts = null;
    private StructMotionFeatures mStructMotionFeatures = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        Intent i = getIntent();

        mSpeedDisplay = (TextView) findViewById(R.id.speedDisplay);
        mMotionInfo = (TextView) findViewById(R.id.motionInfo);

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
                //Creation ou reinitialisation du Vector
                if (mVector == null) {
                    mVector = new Vector();
                } else {
                    mVector.clear();
                }
                //Creation ou reinitialisation du StructMotionElemts
                if (mStructMotionElemts == null) {
                    mStructMotionElemts = new StructMotionElemts();
                } else {
                    mStructMotionElemts.clear();
                }
                //TODO retrieve from global variable instead of creation
                if (mStructMotionFeatures == null) {
                    mStructMotionFeatures = new StructMotionFeatures();
                }
                else{
                    mStructMotionFeatures.clear();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                mStructMotionElemts.compute(event,mVector,mVelocityTracker);
                mSpeedDisplay.setText(mStructMotionElemts.toString());
                break;

            //Exiting event, going though the vector by its iterator and making calulations
            case MotionEvent.ACTION_UP:
                mStructMotionFeatures.compute(mVector);
                mMotionInfo.setText("MOTION EVENT OVERALL VALUES\n" +
                        "Absolute Length : " + mStructMotionFeatures.getMotionAbsLength() + " px\n" +
                        "Total length : " + mStructMotionFeatures.getMotionLength() + " px\n" +
                        "Duration : " + mStructMotionFeatures.getMotionDuration() + " ms\n" +
                        "Avg speed : " + mStructMotionFeatures.getMotionAvgSpeed() + " px/s\n" +
                        "Avg pressure : " + mStructMotionFeatures.getMotionAvgPressure() );
                break;
        }
        return true;
    }
}

