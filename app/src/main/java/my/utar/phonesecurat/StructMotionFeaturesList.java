package my.utar.phonesecurat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Thibault on 10/17/2017.
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
        //Taille de la liste
        int size = this.size();
        dest.writeInt(size);
        for(int i=0; i < size; i++)
        {
            StructMotionFeatures smfl = this.get(i); //On vient lire chaque objet personne
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
        // On vide la liste avant tout remplissage
        this.clear();

        //Récupération du nombre d'objet
        int size = in.readInt();

        //On repeuple la liste avec de nouveau objet
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




