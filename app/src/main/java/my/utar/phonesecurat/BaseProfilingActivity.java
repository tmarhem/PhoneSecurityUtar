package my.utar.phonesecurat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

public class BaseProfilingActivity extends Activity {

    public VelocityTracker mVelocityTracker = null;
    private TextView mSpeedDisplay = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);
        //Test on GitHub
        mSpeedDisplay = (TextView) findViewById(R.id.speedDisplay);

    }
    public boolean onTouchEvent(MotionEvent event){
        mVelocityTracker = VelocityTracker.obtain();
        mSpeedDisplay.setText(mVelocityTracker.toString());
        return true;
    }
}

