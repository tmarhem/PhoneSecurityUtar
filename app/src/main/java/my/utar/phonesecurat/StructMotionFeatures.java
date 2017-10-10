package my.utar.phonesecurat;

import java.util.Vector;

/**
 * Created by Thibault on 10/10/2017.
 */

public class StructMotionFeatures {
    private double motionAbsLength;
    private long motionLength;
    private long motionDuration;
    private double motionAvgSpeed;
    private float avgPressure;
    private float firstPosX, lastPosX, firstPosY, lastPosY;
    private Vector<StructMotionElemts> mVector;
}
