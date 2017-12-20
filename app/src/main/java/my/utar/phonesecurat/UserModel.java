package my.utar.phonesecurat;

/**
 * Computed model used to compare with user moves
 * Created by Thibault on 10/17/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class UserModel implements Parcelable {
    private double avgAbsLength;
    private long avgLength;
    private long avgDuration;
    private double avgSpeed;
    private double avgPressure;
    private int isComputed;

    /**
     * Required for PARCELABLE
     */
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    /**
     * Required for PARCELABLE
     * @param in Parcel
     */
    public UserModel(Parcel in) {
        avgAbsLength = in.readDouble();
        avgLength = in.readLong();
        avgDuration = in.readLong();
        avgSpeed = in.readDouble();
        avgPressure = in.readDouble();
        isComputed = in.readInt();
    }

    public UserModel() {
        this.avgAbsLength = 0;
        this.avgLength = 0;
        this.avgDuration = 0;
        this.avgSpeed = 0;
        this.avgPressure = 0;
        this.isComputed = 0;
    }

    public void clear() {
        this.avgAbsLength = 0;
        this.avgLength = 0;
        this.avgDuration = 0;
        this.avgSpeed = 0;
        this.avgPressure = 0;
        this.isComputed = 0;
    }

    /**
     * Compute the model out of the moves list
     * @param mMotionList
     */
    public void compute(ArrayList<StructMotionFeatures> mMotionList) {
        int i = 0;
        double sumAbsLength = 0;
        long sumLength = 0;
        long sumDuration = 0;
        double sumSpeed = 0;
        double sumPressure = 0;

        while (i <= (mMotionList.size() - 1)) {
            sumAbsLength += mMotionList.get(i).getMotionAbsLength();
            sumLength += mMotionList.get(i).getMotionLength();
            sumDuration += mMotionList.get(i).getMotionDuration();
            sumSpeed += mMotionList.get(i).getMotionAvgSpeed();
            sumPressure += mMotionList.get(i).getMotionAvgPressure();

            i++;
        }
        this.setAvgAbsLength(sumAbsLength / mMotionList.size());
        this.setAvgLength(sumLength / mMotionList.size());
        this.setAvgDuration(sumDuration / mMotionList.size());
        this.setAvgSpeed(sumSpeed / mMotionList.size());
        this.setAvgPressure(sumPressure / mMotionList.size());

        this.setIsComputed(1);
    }

    public int getIsComputed() {
        return isComputed;
    }

    public void setIsComputed(int isComputed) {
        this.isComputed = isComputed;
    }

    public double getAvgAbsLength() {
        return avgAbsLength;
    }

    public void setAvgAbsLength(double avgAbsLength) {
        this.avgAbsLength = avgAbsLength;
    }

    public long getAvgLength() {
        return avgLength;
    }

    public void setAvgLength(long avgLength) {
        this.avgLength = avgLength;
    }

    public long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(long avgDuration) {
        this.avgDuration = avgDuration;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getAvgPressure() {
        return avgPressure;
    }

    public void setAvgPressure(double avgPressure) {
        this.avgPressure = avgPressure;
    }

    /**
     * Required for PARCELABLE
     * @return 0
     */
    @Override
    public int describeContents() {
        //Return 0 as our object doesn't have FileDescriptor objects
        return 0;
    }

    /**
     * Required for PARCELABLE
     * @param dest Parcel
     * @param flags int
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //We add object the same order they're declared
        dest.writeDouble(avgAbsLength);
        dest.writeLong(avgLength);
        dest.writeLong(avgDuration);
        dest.writeDouble(avgSpeed);
        dest.writeDouble(avgPressure);
    }
}
