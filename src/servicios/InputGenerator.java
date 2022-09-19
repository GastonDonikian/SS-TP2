package servicios;

import models.Particle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InputGenerator extends main {
    Integer numberOfParticles;
    Double xLength;
    Double yLength;
    Double cavitySize;
    Double radiusLimit;
    Double speed;
    Double weight;
    List<Particle> particles = new ArrayList<>();

    public InputGenerator(Integer numberOfParticles, Double xLength, Double yLength, Double cavitySize, Double radiusLimit, Double speed, Double weight) {
        this.numberOfParticles = numberOfParticles;
        this.xLength = xLength;
        this.yLength = yLength;
        this.cavitySize = cavitySize;
        this.radiusLimit = radiusLimit;
        this.speed = speed;
        this.weight = weight;
    }

    public void generate() throws IOException {
        this.generateParticleList();
        this.staticFile();
        this.dynamicFile();
    }

    public void staticFile() throws IOException {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(numberOfParticles.toString());
        stringBuilder.append("\n");
        stringBuilder.append(xLength);
        stringBuilder.append("\n");
        stringBuilder.append(yLength);
        stringBuilder.append("\n");
        stringBuilder.append(cavitySize);
        stringBuilder.append("\n");
        for(int i = 0; i < numberOfParticles; i++) {
            stringBuilder.append(radiusLimit);
            stringBuilder.append(" ");
            stringBuilder.append(weight);
            stringBuilder.append("\n");
        }
        File file = new File("./resources/staticFile");
        FileWriter fileWriter = new FileWriter("./resources/staticFile");
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();
    }

    public void dynamicFile() throws IOException {
        Double auxAngle;
        StringBuilder stringBuilder = new StringBuilder("t0\n");

        for(int i = 0; i < numberOfParticles; i++) {
            Particle p = particles.get(i);
            stringBuilder.append(p.getX());
            stringBuilder.append(" ");
            stringBuilder.append(p.getY());
            stringBuilder.append(" ");
            auxAngle = Math.random()*2*Math.PI;
            stringBuilder.append(Math.cos(auxAngle)*speed);
            stringBuilder.append(" ");
            stringBuilder.append(Math.sin(auxAngle)*speed);

            stringBuilder.append("\n");
        }
        File file = new File("./resources/dynamicFile");
        FileWriter fileWriter = new FileWriter("./resources/dynamicFile");
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();
    }

    private void generateParticleList() {
        while (particles.size() < numberOfParticles) {
            double possibleX = Math.random()*xLength/2.0;
            double possibleY = Math.random()*yLength;
            if (checkCoordinates(possibleX, possibleY)) {
                Particle p = new Particle(possibleX, possibleY, radiusLimit, particles.size() + 1, Math.random()*2*Math.PI, weight);
                p.setVelocity(speed, p.getAngle());
                particles.add(p);
            }
        }
    }

    private boolean checkCoordinates(double x, double y) {
        return checkWalls(x, y) && checkParticles(x, y);
    }

    private boolean checkWalls(double x, double y){
        double leftWallX = 0;
        double middleWallX = xLength/2;
        double bottomWallY = 0;
        double topWallY = yLength;

        return !(y + radiusLimit >= topWallY) && !(y - radiusLimit <= bottomWallY) && !(x + radiusLimit >= middleWallX) && !(x - radiusLimit <= leftWallX);
    }

    private boolean checkParticles(double x, double y){
        for (Particle particle : particles) {
            Double x2 = Math.pow(particle.getX() - x, 2);
            Double y2 = Math.pow(particle.getY() - y, 2);
            if (x2 + y2 <= Math.pow(2*radiusLimit, 2)){
                return false;
            }
        }
        return true;
    }
}
