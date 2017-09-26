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

    //Accesseurs - Accessibility methods
    public float getPosX(){
        return this.posX;
    }

    public float getPosY(){
        return this.posY;
    }

    public long getTime(){
        return this.time;
    }

    public float getPressure(){
        return this.pressure;
    }

    public double getSpeed(){
        return this.speed;
    }

    //Utility methods
    public boolean clear(){
        this.posX = 0;
        this.posY = 0;
        this.speed = 0;
        this.pressure = 0;
        this.time = 0;

        if(     this.posX == 0 &&
                this.posY == 0 &&
                this.speed == 0 &&
                this.time == 0 &&
                this.pressure == 0){
            return true;
        }
        else return false;
    }

    //Setters
    public void setPosX(float posX){
        this.posX = posX;
    }

    public void setPosY(float posY){
        this.posY = posY;
    }

    public void setTime(long time){
        this.time = time;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public String toString(){
        return "posX:" + Float.toString(posX) + " posY:" + Float.toString(posY)+
                " time:" + Long.toString(time) + " pressure:" + Float.toString(pressure)+
                " speed:" + Double.toString(speed);
    }

    public StructMotionElemts clone(){
        return new StructMotionElemts(this.posX, this.posY, this.time, this.pressure, this.speed);
    }
}