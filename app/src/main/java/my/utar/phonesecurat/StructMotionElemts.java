package my.utar.phonesecurat;

/**
 * Created by Thibault on 9/21/2017.
 */

public class StructMotionElemts {
    private float posX;
    private float posY;
    private long time;
    private float pressure;
    private double speed;


    public StructMotionElemts() {
        posX = 0;
        posY = 0;
        time = 0;
        pressure = 0;
        speed = 0;
    }

    public StructMotionElemts(float posX, float posY, long time, float pressure, double speed) {
        this.posX = posX;
        this.posY = posY;
        this.time = time;
        this.pressure = pressure;
        this.speed = speed;
    }

}
