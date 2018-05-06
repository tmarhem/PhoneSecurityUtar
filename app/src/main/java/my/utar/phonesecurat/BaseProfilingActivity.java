package my.utar.phonesecurat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import weka.core.pmml.Array;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;

/**
 * XLS EXPORT
 */
public class BaseProfilingActivity extends Activity {

    private final Context mContext = this;
    private VelocityTracker mVelocityTracker;
    private TextView mTextCounterSwipeRight, mTextCounterSwipeLeft, mTextCounterScollUp, mTextCounterScrollDown;
    private ArrayList<StructMotionElemts> mPointsList;
    private ArrayList<StructMotionFeatures> mSwipeRightList, mSwipeLeftList, mScrollUpList, mScrollDownList;
    private StructMotionElemts mStructMotionElemts;
    private StructMotionFeatures mStructMotionFeatures;
    private GestureDetector gestureDetector;
    private int counterSwipeRight, counterSwipeLeft, counterScrollUp, counterScrollDown;
    private boolean mSwitch, switchScrollUp, switchScrollDown, switchBlockSwipe;
    Button mBtnExport, mBtnReset;


    /**
     * Method launched on the creation of the activity
     * Retrieves and initialize everything that is needed
     *
     * @param savedInstanceState Bundle that saves information in case of sudden shutdown of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profiling);

        if (android.os.Build.VERSION.SDK_INT>=23) {
            String myChain[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(myChain, 1111);
        }


        mSwitch = false;
        switchScrollUp = false;
        switchScrollDown = false;
        switchBlockSwipe = false;

        mSwipeRightList = new ArrayList<>();
        mSwipeLeftList = new ArrayList<>();
        mScrollUpList = new ArrayList<>();
        mScrollDownList = new ArrayList<>();

        refreshCounters();

        mTextCounterSwipeRight = findViewById(R.id.counterSwipeRight);
        mTextCounterSwipeLeft = findViewById(R.id.counterSwipeLeft);
        mTextCounterScollUp = findViewById(R.id.counterScrollUp);
        mTextCounterScrollDown = findViewById(R.id.counterScrollDown);

        refreshCountersDisplays();

        mBtnExport = findViewById(R.id.btnExport);
        mBtnReset = findViewById(R.id.btnReset);

        gestureDetector = new GestureDetector(mContext, new GestureListener());


        mBtnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final EditText fileNAME = findViewById(R.id.fileName);
                String fname = fileNAME.getText().toString();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(fname);
                alertDialogBuilder
                        .setMessage("File will be saved to " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                        .setCancelable(true)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                writeToXls();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder
                        .setMessage("Do you want to reset this set of moves without saving ?" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                        .setCancelable(true)
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSwipeRightList.clear();
                                mSwipeLeftList.clear();
                                mScrollUpList.clear();
                                mScrollDownList.clear();

                                refreshCounters();
                                refreshCountersDisplays();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    public void writeToXls() {
        WritableWorkbook myFirstWbook = null;
        try {
            EditText fileNAME = findViewById(R.id.fileName);
            String fname = fileNAME.getText().toString();
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "/" + fname);
            myFirstWbook = Workbook.createWorkbook(file);
            // create an Excel sheet
            WritableSheet excelSheet = myFirstWbook.createSheet("Sheet 1", 0);

            int i;
            int rowIndex = 1;

            // add something into the Excel sheet
            excelSheet.addCell(new Label(0, 0, "Length"));
            excelSheet.addCell(new Label(1, 0, "Absolute Length"));
            excelSheet.addCell(new Label(2, 0, "Duration"));
            excelSheet.addCell(new Label(3, 0, "Avg Speed"));
            excelSheet.addCell(new Label(4, 0, "Avg Pressure"));
            excelSheet.addCell(new Label(5, 0, "Move Type"));
            excelSheet.addCell(new Label(6, 0, "UserID"));
            excelSheet.addCell(new Label(7, 0, "PhoneID"));
            excelSheet.addCell(new Label(8, 0, "Start x"));
            excelSheet.addCell(new Label(9, 0, "Start y"));
            excelSheet.addCell(new Label(10, 0, "End x"));
            excelSheet.addCell(new Label(11, 0, "End y"));
            excelSheet.addCell(new Label(12, 0, "Start Pressure"));
            excelSheet.addCell(new Label(13, 0, "End Pressure"));



            for (i = 0; i < mSwipeRightList.size(); i++) {
                excelSheet.addCell(new Number(0, rowIndex, mSwipeRightList.get(i).getMotionLength()));
                excelSheet.addCell(new Number(1, rowIndex, mSwipeRightList.get(i).getMotionAbsLength()));
                excelSheet.addCell(new Number(2, rowIndex, mSwipeRightList.get(i).getMotionDuration()));
                excelSheet.addCell(new Number(3, rowIndex, mSwipeRightList.get(i).getMotionAvgSpeed()));
                excelSheet.addCell(new Number(4, rowIndex, mSwipeRightList.get(i).getMotionAvgPressure()));
                excelSheet.addCell(new Label(5, rowIndex, mSwipeRightList.get(i).getMoveType()));
                excelSheet.addCell(new Label(6, rowIndex, mSwipeRightList.get(i).getUserId()));
                excelSheet.addCell(new Label(7, rowIndex, mSwipeRightList.get(i).getPhoneId()));
                excelSheet.addCell(new Label(8, rowIndex, Float.toString(mSwipeRightList.get(i).getFirstPosX())));
                excelSheet.addCell(new Label(9, rowIndex, Float.toString(mSwipeRightList.get(i).getFirstPosY())));
                excelSheet.addCell(new Label(10, rowIndex, Float.toString(mSwipeRightList.get(i).getLastPosX())));
                excelSheet.addCell(new Label(11, rowIndex, Float.toString(mSwipeRightList.get(i).getLastPosY())));
                excelSheet.addCell(new Label(12, rowIndex, Double.toString(mSwipeRightList.get(i).getFirstPressure())));
                excelSheet.addCell(new Label(13, rowIndex, Double.toString(mSwipeRightList.get(i).getLastPressure())));



                rowIndex++;
            }

            for (i = 0; i < mSwipeLeftList.size(); i++) {
                excelSheet.addCell(new Number(0, rowIndex, mSwipeLeftList.get(i).getMotionLength()));
                excelSheet.addCell(new Number(1, rowIndex, mSwipeLeftList.get(i).getMotionAbsLength()));
                excelSheet.addCell(new Number(2, rowIndex, mSwipeLeftList.get(i).getMotionDuration()));
                excelSheet.addCell(new Number(3, rowIndex, mSwipeLeftList.get(i).getMotionAvgSpeed()));
                excelSheet.addCell(new Number(4, rowIndex, mSwipeLeftList.get(i).getMotionAvgPressure()));
                excelSheet.addCell(new Label(5, rowIndex, mSwipeLeftList.get(i).getMoveType()));
                excelSheet.addCell(new Label(6, rowIndex, mSwipeLeftList.get(i).getUserId()));
                excelSheet.addCell(new Label(7, rowIndex, mSwipeLeftList.get(i).getUserId()));
                excelSheet.addCell(new Label(8, rowIndex, Float.toString(mSwipeLeftList.get(i).getFirstPosX())));
                excelSheet.addCell(new Label(9, rowIndex, Float.toString(mSwipeLeftList.get(i).getFirstPosY())));
                excelSheet.addCell(new Label(10, rowIndex, Float.toString(mSwipeLeftList.get(i).getLastPosX())));
                excelSheet.addCell(new Label(11, rowIndex, Float.toString(mSwipeLeftList.get(i).getLastPosY())));
                excelSheet.addCell(new Label(12, rowIndex, Double.toString(mSwipeLeftList.get(i).getFirstPressure())));
                excelSheet.addCell(new Label(13, rowIndex, Double.toString(mSwipeLeftList.get(i).getLastPressure())));
                rowIndex++;
            }

            for (i = 0; i < mScrollUpList.size(); i++) {
                excelSheet.addCell(new Number(0, rowIndex, mScrollUpList.get(i).getMotionLength()));
                excelSheet.addCell(new Number(1, rowIndex, mScrollUpList.get(i).getMotionAbsLength()));
                excelSheet.addCell(new Number(2, rowIndex, mScrollUpList.get(i).getMotionDuration()));
                excelSheet.addCell(new Number(3, rowIndex, mScrollUpList.get(i).getMotionAvgSpeed()));
                excelSheet.addCell(new Number(4, rowIndex, mScrollUpList.get(i).getMotionAvgPressure()));
                excelSheet.addCell(new Label(5, rowIndex, mScrollUpList.get(i).getMoveType()));
                excelSheet.addCell(new Label(6, rowIndex, mScrollUpList.get(i).getUserId()));
                excelSheet.addCell(new Label(7, rowIndex, mScrollUpList.get(i).getUserId()));
                excelSheet.addCell(new Label(8, rowIndex, Float.toString(mScrollUpList.get(i).getFirstPosX())));
                excelSheet.addCell(new Label(9, rowIndex, Float.toString(mScrollUpList.get(i).getFirstPosY())));
                excelSheet.addCell(new Label(10, rowIndex, Float.toString(mScrollUpList.get(i).getLastPosX())));
                excelSheet.addCell(new Label(11, rowIndex, Float.toString(mScrollUpList.get(i).getLastPosY())));
                excelSheet.addCell(new Label(12, rowIndex, Double.toString(mScrollUpList.get(i).getFirstPressure())));
                excelSheet.addCell(new Label(13, rowIndex, Double.toString(mScrollUpList.get(i).getLastPressure())));
                rowIndex++;
            }

            for (i = 0; i < mScrollDownList.size(); i++) {
                excelSheet.addCell(new Number(0, rowIndex, mScrollDownList.get(i).getMotionLength()));
                excelSheet.addCell(new Number(1, rowIndex, mScrollDownList.get(i).getMotionAbsLength()));
                excelSheet.addCell(new Number(2, rowIndex, mScrollDownList.get(i).getMotionDuration()));
                excelSheet.addCell(new Number(3, rowIndex, mScrollDownList.get(i).getMotionAvgSpeed()));
                excelSheet.addCell(new Number(4, rowIndex, mScrollDownList.get(i).getMotionAvgPressure()));
                excelSheet.addCell(new Label(5, rowIndex, mScrollDownList.get(i).getMoveType()));
                excelSheet.addCell(new Label(6, rowIndex, mScrollDownList.get(i).getUserId()));
                excelSheet.addCell(new Label(7, rowIndex, mScrollDownList.get(i).getUserId()));
                excelSheet.addCell(new Label(8, rowIndex, Float.toString(mScrollDownList.get(i).getFirstPosX())));
                excelSheet.addCell(new Label(9, rowIndex, Float.toString(mScrollDownList.get(i).getFirstPosY())));
                excelSheet.addCell(new Label(10, rowIndex, Float.toString(mScrollDownList.get(i).getLastPosX())));
                excelSheet.addCell(new Label(11, rowIndex, Float.toString(mScrollDownList.get(i).getLastPosY())));
                excelSheet.addCell(new Label(12, rowIndex, Double.toString(mScrollDownList.get(i).getFirstPressure())));
                excelSheet.addCell(new Label(13, rowIndex, Double.toString(mScrollDownList.get(i).getLastPressure())));
                rowIndex++;
            }

            myFirstWbook.write();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }


        }
    }


    /**
     * Extending the existing class SimpleOnGestureListener to our needs, customization of thresholds and recognized movements
     */
    private class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 45;
        private static final int SWIPE_VELOCITY_THRESHOLD = 45;
        private static final int SCROLL_VELOCITY_THRESHOLD = 15;


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * Overriding existing method on SimpleOnGestureListener that triggers on a Swipe
         * Computing speed and direction to launch SwipeRight() or SwipeLeft()
         *
         * @param e1        MotionEvent
         * @param e2        Motion Event
         * @param velocityX X axis instant Velocity
         * @param velocityY Y axis instant velocity
         * @return boolean true if movement considered onFling after checking thresholds
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (!switchBlockSwipe) {
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distanceY = e2.getY() - e1.getY();

            if (Math.abs(velocityY) > SCROLL_VELOCITY_THRESHOLD) {
                if (distanceY < 0) {
                    switchScrollUp = true;
                } else if (distanceY > 0) {
                    switchScrollDown = true;
                }
                return true;
            }
            return false;
        }
    }


    /**
     * Method that triggers when you touch the screen
     *
     * @param event MotionEvent
     * @return boolean
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
/*
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
                mStructMotionElemts.compute(event, mPointsList, mVelocityTracker);
                //DISPLAY
                break;

            case MotionEvent.ACTION_UP:
                if (mSwitch) {
                    mStructMotionFeatures.compute(mPointsList);
                    mSwitch = false;
                    switchBlockSwipe = false;

                    float distanceY = mStructMotionFeatures.getLastPosY() - mStructMotionFeatures.getFirstPosY();
                    float distanceX = mStructMotionFeatures.getLastPosX() - mStructMotionFeatures.getFirstPosX();
                    double angle = Math.toDegrees(Math.atan(distanceY / distanceX));

                    if (switchScrollUp && !((angle > -50.0) && (angle < 50.0))) {
                        switchScrollUp = false;
                        switchBlockSwipe = true;
                        onScrollUp();
                    }


                    if (switchScrollDown && !((angle > -50.0) && (angle < 50.0))) {
                        switchScrollDown = false;
                        switchBlockSwipe = true;
                        onScrollDown();
                    }

                    switchScrollDown = false;
                    switchScrollUp = false;
                }
                break;
        }
        //Used at the end of a motionEvent move to trigger OnFling method
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * If movement is recognized as right swipe, add movement to the list
     * When sample max number is reached, model is computed
     * If model is already computed, goes to comparison
     */
    public void onSwipeRight() {
        EditText userID = findViewById(R.id.userID);
        String userId = userID.getText().toString();

        EditText phoneID = findViewById(R.id.phoneID);
        String phoneId = phoneID.getText().toString();

        mStructMotionFeatures.setMoveType("swipeRight");
        mStructMotionFeatures.setUserId(userId);
        mStructMotionFeatures.setPhoneId(phoneId);
        mSwipeRightList.add(mStructMotionFeatures.clone());
        counterSwipeRight++;
        refreshCountersDisplays();
    }


    public void onSwipeLeft() {
        EditText userID = findViewById(R.id.userID);
        String userId = userID.getText().toString();

        EditText phoneID = findViewById(R.id.phoneID);
        String phoneId = phoneID.getText().toString();

        mStructMotionFeatures.setMoveType("swipeLeft");
        mStructMotionFeatures.setUserId(userId);
        mStructMotionFeatures.setPhoneId(phoneId);
        mSwipeLeftList.add(mStructMotionFeatures.clone());
        counterSwipeLeft++;
        refreshCountersDisplays();
    }

    public void onScrollUp() {
        EditText userID = findViewById(R.id.userID);
        String userId = userID.getText().toString();

        EditText phoneID = findViewById(R.id.phoneID);
        String phoneId = phoneID.getText().toString();

        mStructMotionFeatures.setMoveType("scrollUp");
        mStructMotionFeatures.setUserId(userId);
        mStructMotionFeatures.setPhoneId(phoneId);
        mScrollUpList.add(mStructMotionFeatures.clone());
        counterScrollUp++;
        refreshCountersDisplays();
    }

    public void onScrollDown() {
        EditText userID = findViewById(R.id.userID);
        String userId = userID.getText().toString();

        EditText phoneID = findViewById(R.id.phoneID);
        String phoneId = phoneID.getText().toString();

        mStructMotionFeatures.setMoveType("scrollDown");
        mStructMotionFeatures.setUserId(userId);
        mStructMotionFeatures.setPhoneId(phoneId);
        mScrollDownList.add(mStructMotionFeatures.clone());
        counterScrollDown++;
        refreshCountersDisplays();
    }

    public void refreshCountersDisplays() {
        mTextCounterSwipeRight.setText(Integer.toString(counterSwipeRight));
        mTextCounterSwipeLeft.setText(Integer.toString(counterSwipeLeft));
        mTextCounterScollUp.setText(Integer.toString(counterScrollUp));
        mTextCounterScrollDown.setText(Integer.toString(counterScrollDown));
    }

    public void refreshCounters(){
        counterSwipeRight = mSwipeRightList.size();
        counterSwipeLeft = mSwipeLeftList.size();
        counterScrollUp = mScrollUpList.size();
        counterScrollDown = mScrollDownList.size();
    }
    
    
}

