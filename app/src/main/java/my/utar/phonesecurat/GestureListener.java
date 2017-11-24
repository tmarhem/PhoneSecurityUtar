package my.utar.phonesecurat;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 *Extending the existing class SimpleOnGestureListener to our need, customization of thresholds and recognized movements
 */
abstract class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_DISTANCE_THRESHOLD = 66;
    private static final int SWIPE_VELOCITY_THRESHOLD = 66;

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    /**
     * Overriding existing method on SimpleOnGestureListener that triggers on a Swipe
     * Computing speed and direction to launch SwipeRight() or SwipeLeft()
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0)
                onSwipeRight();
            else
                onSwipeLeft();
            return true;
        }
        return false;
    }

    public void onSwipeRight(){

    }

    public void onSwipeLeft(){

    }

}
