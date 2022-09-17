package models;

public class Particle {

    private Double x;
    private Double y;
    private final Double radius; //Este es el radio de la particula, no de la proximidad que tan "ancha" es
    private int id;
    private Double angle;
    private Double xSpeed;
    private Double ySpeed;
    private Double weight;


    public Particle(Double x, Double y, Double radius, int id, double angle, Double weight) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
        this.angle = angle;
        this.weight = weight;
    }

    public void setCoordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Particle(Double radius, double weight, int id) {
        this.radius = radius;
        this.weight = weight;
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getRadius() {
        return radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                ", Vx=" + xSpeed +
                ", Vy=" + ySpeed +
                ", angle=" + angle + " rad" +
                ", angle=" + angle * (180 / Math.PI) + "º" +
                '}' + '\n';
    }
    public void setVelocity(double speed,double angle){
        setAngle(angle);
        setXSpeed(speed);
        setYSpeed(speed);
    }

    private void setAngle(double angle){
        this.angle = angle;
    }

    public void calculateAngle(double xSpeed, double ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.angle = Math.atan2(this.ySpeed, this.xSpeed);
    }

    private void setXSpeed(double speed) {
        this.xSpeed = speed * Math.cos(this.angle);
    }

    private void setYSpeed(double speed) {
        this.ySpeed = speed * Math.sin(this.angle);
    }

    public Double getAngle() {
        return angle;
    }

    public Double getXSpeed() {
        return xSpeed;
    }

    public Double getYSpeed() {
        return ySpeed;
    }

    public Double getWeight() {
        return weight;
    }

    public void setXSpeed(Double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(Double ySpeed) {
        this.ySpeed = ySpeed;
    }
}
