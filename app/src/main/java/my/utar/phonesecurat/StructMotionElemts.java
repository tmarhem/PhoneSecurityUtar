package my.utar.phonesecurat;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * StructMotionElemts class
 * This class store a sample of all the raw data collectible from a move at one specific time
 * Created by Thibault on 9/21/2017.
 */

public class StructMotionElemts implements Parcelable{
    private float posX;
    private float posY;
    private long time;
    private float pressure;
    private double speed;
    private float size;

    public static final Parcelable.Creator<StructMotionElemts> CREATOR = new Parcelable.Creator<StructMotionElemts>(){
        @Override
        public StructMotionElemts createFromParcel(Parcel source){
            return new StructMotionElemts(source);
        }

        @Override
        public StructMotionElemts[] newArray(int size){
            return new StructMotionElemts[size];
        }
    };

    public StructMotionElemts(Parcel in){
        posX = in.readFloat();
        posY = in.readFloat();
        time = in.readLong();
        pressure = in.readFloat();
        speed = in.readDouble();
        size = in.readFloat();
    }


    protected StructMotionElemts() {
        posX = 0;
        posY = 0;
        time = 0;
        pressure = 0;
        speed = 0;
        size = 0;
    }

    protected StructMotionElemts(float posX, float posY, long time, float pressure, double speed, float size) {
        this.posX = posX;
        this.posY = posY;
        this.time = time;
        this.pressure = pressure;
        this.speed = speed;
        this.size = size;
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public long getTime() {
        return this.time;
    }

    public float getPressure() {
        return this.pressure;
    }

    public double getSpeed() {
        return this.speed;
    }

    public float getSize() {
        return this.size;
    }

    public void clear() {
        this.posX = 0;
        this.posY = 0;
        this.speed = 0;
        this.pressure = 0;
        this.time = 0;
        this.size = 0;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Retrieves MotionEvent features into the StructMotionElmts and add it to the Motion List
     *
     * @param event            related MotionEvent
     * @param mList          related List
     * @param mVelocityTracker related VelocityTracker
     */
    public void compute(MotionEvent event, ArrayList<StructMotionElemts> mList, VelocityTracker mVelocityTracker) {
        // Compute X, Y, time, pressure & instantSpeed

        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);

        this.setSpeed(sqrt(pow(mVelocityTracker.getXVelocity(), 2) +
                pow(mVelocityTracker.getYVelocity(), 2)));
        this.setPosX(event.getX());
        this.setPosY(event.getY());
        this.setPressure(event.getPressure());
        this.setSize(event.getSize());
        this.setTime(SystemClock.uptimeMillis());
        // Insert object in List
        mList.add(this.clone());
    }

    @Override
    public String toString() {
        return "INSTANT VALUES\nposX : " + Float.toString(posX) + "\nposY : " + Float.toString(posY) +
                "\ntime : " + Long.toString(time) + "\npressure : " + Float.toString(pressure) +
                "\nspeed : " + Double.toString(speed) + "\nsize : " + Float.toString(size);
    }

    @Override
    public StructMotionElemts clone() {
        return new StructMotionElemts(this.posX, this.posY, this.time, this.pressure, this.speed, this.size);
    }

    @Override
    public int describeContents() {
        //Return 0 as our object dosen't have FileDescriptor
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        //We add object the same order they're declared
        dest.writeFloat(posX);
        dest.writeFloat(posY);
        dest.writeLong(time);
        dest.writeFloat(pressure);
        dest.writeDouble(speed);
        dest.writeFloat(size);
    }
}