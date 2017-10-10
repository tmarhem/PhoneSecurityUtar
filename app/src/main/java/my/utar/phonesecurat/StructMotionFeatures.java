package my.utar.phonesecurat;

import java.util.Iterator;
import java.util.Vector;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
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
        mVectorList = new Vector();
    }

    public void clear() {
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

    public void compute(Vector<StructMotionElemts> mVector) {
        //Resets and utility
        double sumSpeed = 0;
        double sumPressure = 0;
        float nowPosX = 0;
        float nowPosY = 0;
        float prevPosX = 0;
        float prevPosY = 0;
        long startTime = 0;
        long endTime = 0;
        StructMotionElemts mStructMotionElemts = new StructMotionElemts();

        //Initialisation bourrine de nowPos
        if (!mVector.isEmpty()) {

            mStructMotionElemts = (StructMotionElemts) mVector.firstElement();
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
            mStructMotionElemts = (StructMotionElemts) mVector.firstElement();
            firstPosX = mStructMotionElemts.getPosX();
            firstPosY = mStructMotionElemts.getPosY();
            startTime = mStructMotionElemts.getTime();
            mStructMotionElemts = (StructMotionElemts) mVector.get(mVector.size() - 1);
            lastPosX = mStructMotionElemts.getPosX();
            lastPosY = mStructMotionElemts.getPosY();
            endTime = mStructMotionElemts.getTime();
            //Computing
            motionDuration = endTime - startTime;
            motionAbsLength = sqrt(pow((lastPosX - firstPosX), 2) + pow((lastPosY - firstPosY), 2));


        } else {
            //Log.v("TEST", "\n Vecteur vide\n");
        }
    }


}
