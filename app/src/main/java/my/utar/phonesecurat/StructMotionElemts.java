package my.utar.phonesecurat;

/**
 * Created by Thibault on 9/21/2017.
 */

public class StructMotionElemts {
    private long posX;
    private long posY;
    private long time;
    private double pressure;
    private long speed;


    public StructMotionElemts() {
        posX = 0;
        posY = 0;
        time = 0;
        pressure = 0;
        speed = 0;
    }

    public StructMotionElemts(long posX, long posY, long time, double pressure, long speed) {
        this.posX = posX;
        this.posY = posY;
        this.time = time;
        this.pressure = pressure;
        this.speed = speed;
    }

}
