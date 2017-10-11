package my.utar.phonesecurat;

import java.util.Iterator;
import java.util.Vector;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/*
 * Created by Thibault on 10/10/2017.
 */

public class StructMotionFeatures {
    private double motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;
    private double motionAvgPressure;
    private float firstPosX, lastPosX, firstPosY, lastPosY;
    private Vector<StructMotionElemts> mVectorList;


    public StructMotionFeatures() {
        motionAbsLength = 0;
        motionLength = 0;
        motionDuration = 0;
        motionAvgSpeed = 0;
        motionAvgSpeed = 0;
        motionAvgPressure = 0;
        firstPosX = 0;
        lastPosX = 0;
        firstPosY = 0;
        lastPosY = 0;
        mVectorList = new Vector<>();
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

    public float getFirstPosX() {
        return firstPosX;
    }

    public void setFirstPosX(float firstPosX) {
        this.firstPosX = firstPosX;
    }

    public float getLastPosX() {
        return lastPosX;
    }

    public void setLastPosX(float lastPosX) {
        this.lastPosX = lastPosX;
    }

    public float getFirstPosY() {
        return firstPosY;
    }

    public void setFirstPosY(float firstPosY) {
        this.firstPosY = firstPosY;
    }

    public float getLastPosY() {
        return lastPosY;
    }

    public void setLastPosY(float lastPosY) {
        this.lastPosY = lastPosY;
    }

    public Vector<StructMotionElemts> getmVectorList() {
        return mVectorList;
    }

    public void setmVectorList(Vector<StructMotionElemts> mVectorList) {
        this.mVectorList = mVectorList;
    }

    protected void clear() {
        motionAbsLength = 0;
        motionLength = 0;
        motionDuration = 0;
        motionAvgSpeed = 0;
        motionAvgSpeed = 0;
        motionAvgPressure = 0;
        firstPosX = 0;
        lastPosX = 0;
        firstPosY = 0;
        lastPosY = 0;
        mVectorList = null;
    }


    protected void compute(Vector<StructMotionElemts> mVector) {
        //Resets and utility
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
        if (!mVector.isEmpty()) {

            mStructMotionElemts = mVector.firstElement();
            nowPosX = mStructMotionElemts.getPosX();
            nowPosY = mStructMotionElemts.getPosY();
            mStructMotionElemts.clear();

            mVector.remove(0);

            //Iterator out of vector
            Iterator i = mVector.iterator();
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

                //DISPLAY//////////////////////
                //Log.v("TEST", mStructMotionElemts.toString());
                ///////////////////////////////
            }
            //motionAvgSpeed & pressure
            motionAvgSpeed = sumSpeed / mVector.size();
            motionAvgPressure = sumPressure / mVector.size();
            //motionDuration & motionAbsLength
            //Retrieving
            mStructMotionElemts = mVector.firstElement();
            firstPosX = mStructMotionElemts.getPosX();
            firstPosY = mStructMotionElemts.getPosY();
            startTime = mStructMotionElemts.getTime();
            mStructMotionElemts = mVector.get(mVector.size() - 1);
            lastPosX = mStructMotionElemts.getPosX();
            lastPosY = mStructMotionElemts.getPosY();
            endTime = mStructMotionElemts.getTime();
            //Computing
            motionDuration = endTime - startTime;
            motionAbsLength = sqrt(pow((lastPosX - firstPosX), 2) + pow((lastPosY - firstPosY), 2));

            mVectorList = (Vector) mVector.clone();
        }
    }


}
