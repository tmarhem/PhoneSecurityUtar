package my.utar.phonesecurat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Thibault on 10/17/2017.
 * Array List extended to be Parcelable and sent within the application
 * Composed of a certain number of moves
 * Represents the full raw data sent for computing the model
 */

public class StructMotionFeaturesList extends ArrayList<StructMotionFeatures> implements Parcelable {

    public StructMotionFeaturesList(){

    }

    public StructMotionFeaturesList(Parcel in){
        this.getFromParcel(in);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StructMotionFeaturesList createFromParcel(Parcel in)
        {
            return new StructMotionFeaturesList(in);
        }

        @Override
        public Object[] newArray(int size) {
            return null;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        int size = this.size();
        dest.writeInt(size);
        for(int i=0; i < size; i++)
        {
            StructMotionFeatures smfl = this.get(i);
            dest.writeDouble(smfl.getMotionAbsLength());
            dest.writeLong(smfl.getMotionLength());
            dest.writeLong(smfl.getMotionDuration());
            dest.writeDouble(smfl.getMotionAvgSpeed());
            dest.writeDouble(smfl.getMotionAvgPressure());
            dest.writeFloat(smfl.getFirstPosX());
            dest.writeFloat(smfl.getLastPosX());
            dest.writeFloat(smfl.getFirstPosY());
            dest.writeFloat(smfl.getLastPosY());
        }
    }

    public void getFromParcel(Parcel in)
    {
        this.clear();
        int size = in.readInt();
        for(int i = 0; i < size; i++)
        {
            StructMotionFeatures smf = new StructMotionFeatures();
            smf.setMotionAbsLength(in.readDouble());
            smf.setMotionLength(in.readLong());
            smf.setMotionDuration(in.readLong());
            smf.setMotionAvgSpeed(in.readDouble());
            smf.setMotionAvgPressure(in.readDouble());
            smf.setFirstPosX(in.readFloat());
            smf.setLastPosX(in.readFloat());
            smf.setFirstPosY(in.readFloat());
            smf.setLastPosY(in.readFloat());
            this.add(smf);
        }

    }
}



