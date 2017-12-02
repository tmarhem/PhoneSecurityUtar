package my.utar.phonesecurat;

import android.util.Log;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Class that stores useful computed values for one move out of a sample list
 * Classifies one complete move
 * Created by Thibault on 10/10/2017.
 */

public class StructMotionFeatures {
    private double motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;
    private double motionAvgPressure;
    float firstPosX, lastPosX, firstPosY, lastPosY;


    @Override
    public StructMotionFeatures clone() {
        StructMotionFeatures mClone = new StructMotionFeatures();
        mClone.setMotionAbsLength(this.getMotionAbsLength());
        mClone.setMotionLength(this.getMotionLength());
        mClone.setMotionDuration(this.getMotionDuration());
        mClone.setMotionAvgSpeed(this.getMotionAvgSpeed());
        mClone.setMotionAvgPressure(this.getMotionAvgPressure());
        return mClone;
    }

    public StructMotionFeatures() {
        motionAbsLength = 0;
        motionLength = 0;
        motionDuration = 0;
        motionAvgSpeed = 0;
        motionAvgSpeed = 0;
        motionAvgPressure = 0;
        firstPosX = 0;
        lastPosX =0;
        firstPosY = 0;
        lastPosY = 0;

    }

    public float getFirstPosX() {
        return firstPosX;
    }

    public float getLastPosX() {
        return lastPosX;
    }

    public float getFirstPosY() {
        return firstPosY;
    }

    public float getLastPosY() {
        return lastPosY;
    }

    public double getMotionAbsLength() {
        return motionAbsLength;
    }

    public void setMotionAbsLength(double motionAbsLength) {
        this.motionAbsLength = motionAbsLength;
    }

    public long getMotionLength() {
        return motionLength;
    }

    public void setMotionLength(long motionLength) {
        this.motionLength = motionLength;
    }

    public long getMotionDuration() {
        return motionDuration;
    }

    public void setMotionDuration(long motionDuration) {
        this.motionDuration = motionDuration;
    }

    protected double getMotionAvgSpeed() {
        return motionAvgSpeed;
    }

    public void setMotionAvgSpeed(double motionAvgSpeed) {
        this.motionAvgSpeed = motionAvgSpeed;
    }

    protected double getMotionAvgPressure() {
        return motionAvgPressure;
    }

    public void setMotionAvgPressure(double motionAvgPressure) {
        this.motionAvgPressure = motionAvgPressure;
    }

    protected void clear() {
        motionAbsLength = 0;
        motionLength = 0;
        motionDuration = 0;
        motionAvgSpeed = 0;
        motionAvgSpeed = 0;
        motionAvgPressure = 0;
        firstPosX = 0;
        lastPosX =0;
        firstPosY = 0;
        lastPosY = 0;
    }

    /**
     * Computes values from raw data of a list of points
     * The results define one move
     *
     * @param mList list of point StructMotionElemts
     */
    protected void compute(ArrayList<StructMotionElemts> mList) {
        double sumSpeed = 0;
        double sumPressure = 0;
        float nowPosX;
        float nowPosY;
        float prevPosX;
        float prevPosY;
        long startTime;
        long endTime;

        StructMotionElemts mStructMotionElemts;

        //Initialisation bourrine de nowPos
        if (mList.size() > 0) {

            mStructMotionElemts = mList.get(0);
            nowPosX = mStructMotionElemts.getPosX();
            nowPosY = mStructMotionElemts.getPosY();
            mStructMotionElemts.clear();

            mList.remove(0);

            //Iterator out of List
            Iterator i = mList.iterator();
            while (i.hasNext()) {
                prevPosX = nowPosX;
                prevPosY = nowPosY;
                mStructMotionElemts = (StructMotionElemts) i.next();
                //motionLength calculation
                nowPosX = mStructMotionElemts.getPosX();
                nowPosY = mStructMotionElemts.getPosY();

                motionLength += sqrt(pow((prevPosX - nowPosX), 2) + pow((prevPosY - nowPosY), 2));
                //avgSpeed calculation
                sumSpeed += mStructMotionElemts.getSpeed();
                sumPressure += mStructMotionElemts.getPressure();
            }

            //motionAvgSpeed & pressure
            if (mList.size() > 0) {
                motionAvgSpeed = sumSpeed / mList.size();
                motionAvgPressure = sumPressure / mList.size();
            } else {
                motionAvgSpeed = 0;
                motionAvgPressure = 0;
            }

            if (!mList.isEmpty()) {
                mStructMotionElemts = mList.get(0);
                firstPosX = mStructMotionElemts.getPosX();
                firstPosY = mStructMotionElemts.getPosY();
                startTime = mStructMotionElemts.getTime();
                mStructMotionElemts = mList.get(mList.size() - 1);
                lastPosX = mStructMotionElemts.getPosX();
                lastPosY = mStructMotionElemts.getPosY();
                endTime = mStructMotionElemts.getTime();

                motionDuration = endTime - startTime;
                motionAbsLength = sqrt(pow((lastPosX - firstPosX), 2) + pow((lastPosY - firstPosY), 2));
            } else {
                Log.v("ERROR", "MLIST RETURNED EMPTY, COMPUTING COULD NOT COMPLETE");
            }
        }
    }

    @Override
    public String toString() {
        NumberFormat nf = new DecimalFormat("0.##");

        return "Abs. Length : " + nf.format(this.getMotionAbsLength()) + " px\n" +
                "Total length : " + nf.format(this.getMotionLength()) + " px\n" +
                "Duration : " + nf.format(this.getMotionDuration()) + " ms\n" +
                "Avg speed : " + nf.format(this.getMotionAvgSpeed()) + " px/s\n" +
                "Avg pressure : " + nf.format(this.getMotionAvgPressure());
    }

}
