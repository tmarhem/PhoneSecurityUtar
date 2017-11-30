package my.utar.phonesecurat;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Struct;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Class that stores useful computed values for one move out of a sample list
 * Classifies one complete move
 * Created by Thibault on 10/10/2017.
 */

public class StructMotionFeatures implements Parcelable {
    private double motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;
    private double motionAvgPressure;

    @Override
    public StructMotionFeatures clone() {
        /*try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }*/
        StructMotionFeatures mClone = new StructMotionFeatures();
        mClone.setMotionAbsLength(this.getMotionAbsLength());
        mClone.setMotionLength(this.getMotionLength());
        mClone.setMotionDuration(this.getMotionDuration());
        mClone.setMotionAvgSpeed(this.getMotionAvgSpeed());
        mClone.setMotionAvgPressure(this.getMotionAvgPressure());
        return mClone;
    }

    public static final Parcelable.Creator<StructMotionFeatures> CREATOR = new Parcelable.Creator<StructMotionFeatures>() {
        @Override
        public StructMotionFeatures createFromParcel(Parcel source) {
            return new StructMotionFeatures(source);
        }

        @Override
        public StructMotionFeatures[] newArray(int size) {
            return new StructMotionFeatures[size];
        }
    };

    public StructMotionFeatures(Parcel in) {
        motionAbsLength = in.readDouble();
        motionLength = in.readLong();
        motionDuration = in.readLong();
        motionAvgSpeed = in.readDouble();
        motionAvgPressure = in.readDouble();
    }

    public StructMotionFeatures() {
        motionAbsLength = 0;
        motionLength = 0;
        motionDuration = 0;
        motionAvgSpeed = 0;
        motionAvgSpeed = 0;
        motionAvgPressure = 0;
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
        float firstPosX, lastPosX, firstPosY, lastPosY;

        StructMotionElemts mStructMotionElemts;

        //Initialisation bourrine de nowPos
        if (mList.size()>0) {

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
            //motionDuration & motionAbsLength
            //Retrieving
            //TODO DEBUG
            if(!mList.isEmpty()) {
                mStructMotionElemts = mList.get(0);
                firstPosX = mStructMotionElemts.getPosX();
                firstPosY = mStructMotionElemts.getPosY();
                startTime = mStructMotionElemts.getTime();
                mStructMotionElemts = mList.get(mList.size() - 1);
                lastPosX = mStructMotionElemts.getPosX();
                lastPosY = mStructMotionElemts.getPosY();
                endTime = mStructMotionElemts.getTime();

                //Computing
                motionDuration = endTime - startTime;
                motionAbsLength = sqrt(pow((lastPosX - firstPosX), 2) + pow((lastPosY - firstPosY), 2));
            }
            else{
                Log.v("ERROR","MLIST RETURNED EMPTY, COMPUTING COULD NOT COMPLETE");
            }
        }
    }

    @Override
    public int describeContents() {
        //Return 0 as our object doesn't have FileDescriptor objects
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //We add object the same order they're declared
        dest.writeDouble(motionAbsLength);
        dest.writeLong(motionLength);
        dest.writeLong(motionDuration);
        dest.writeDouble(motionAvgSpeed);
        dest.writeDouble(motionAvgPressure);
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
