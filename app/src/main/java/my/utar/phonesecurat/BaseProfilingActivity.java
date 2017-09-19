package my.utar.phonesecurat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import static java.lang.Math.pow;

public class BaseProfilingActivity extends Activity {

    private VelocityTracker mVelocityTracker = null;
    private TextView mSpeedDisplay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        Intent i = getIntent();

        mSpeedDisplay = (TextView) findViewById(R.id.speedDisplay);

    }
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN :
                if (mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
            break;

            case MotionEvent.ACTION_MOVE :
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                mSpeedDisplay.setText(String.valueOf(pow(mVelocityTracker.getXVelocity(),2)));

        }
        return true;
    }
}

