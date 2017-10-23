package my.utar.phonesecurat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Class that stores useful computed values out of a sample list
 * Classifies one complete move
 * Created by Thibault on 10/10/2017.
 */

public class StructMotionFeatures implements Parcelable{
    private double motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;
    private double motionAvgPressure;
    private float firstPosX, lastPosX, firstPosY, lastPosY;
    private int orientation;
    private int moveType;
    //private ArrayList<StructMotionElemts> mListList;



    public static final Parcelable.Creator<StructMotionFeatures> CREATOR = new Parcelable.Creator<StructMotionFeatures>(){
        @Override
        public StructMotionFeatures createFromParcel(Parcel source){
            return new StructMotionFeatures(source);
        }

        @Override
        public StructMotionFeatures[] newArray(int size){
            return new StructMotionFeatures[size];
        }
    };

    public StructMotionFeatures(Parcel in){
        motionAbsLength = in.readDouble();
        motionLength = in.readLong();
        motionDuration = in.readLong();
        motionAvgSpeed = in.readDouble();
        motionAvgPressure = in.readDouble();
        firstPosX = in.readFloat();
        lastPosX = in.readFloat();
        firstPosY = in.readFloat();
        lastPosX = in.readFloat();
        orientation = in.readInt();
        moveType = in.readInt();

        //mListList = in.readArrayList(StructMotionElemts.class.getClassLoader());



    }

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
        orientation = 0;
        moveType = 0;

        //mListList = new ArrayList<>();
    }

    public double getMotionAbsLength() {
        return motionAbsLength;
    }

    public void setMotionAbsLength(double motionAbsLength) { this.motionAbsLength = motionAbsLength;}

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

    public void setMotionAvgPressure(double motionAvgPressure) { this.motionAvgPressure = motionAvgPressure; }

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

    //public ArrayList<StructMotionElemts> getmListList() {  return mListList; }

    //public void setmListList(ArrayList<StructMotionElemts> mListList) { this.mListList = mListList; }

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
        orientation = 0;
        moveType = 0;
        //mListList = null;
    }

    /**
     * Computes values from raw data of a list of points
     * The results define one move
     * @param mList list of point StructMotionElemts
     */
    protected void compute(ArrayList<StructMotionElemts> mList) {
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
        if (!mList.isEmpty()) {

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

                //DISPLAY//////////////////////
                //Log.v("TEST", mStructMotionElemts.toString());
                ///////////////////////////////
            }
            //motionAvgSpeed & pressure
            motionAvgSpeed = sumSpeed / mList.size();
            motionAvgPressure = sumPressure / mList.size();
            //motionDuration & motionAbsLength
            //Retrieving
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

            //mListList = (ArrayList) mList.clone();
        }
    }

    @Override
    public int describeContents() {
        //Return 0 as our object dosen't have FileDescriptor objects
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        //We add object the same order they're declared
        dest.writeDouble(motionAbsLength);
        dest.writeLong(motionLength);
        dest.writeLong(motionDuration);
        dest.writeDouble(motionAvgSpeed);
        dest.writeDouble(motionAvgPressure);
        dest.writeFloat(firstPosX);
        dest.writeFloat(lastPosX);
        dest.writeFloat(firstPosY);
        dest.writeFloat(lastPosY);
        dest.writeInt(orientation);
        dest.writeInt(moveType);
        //dest.writeList(mListList);

    }

}
