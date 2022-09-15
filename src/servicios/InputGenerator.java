package servicios;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class InputGenerator{
    Integer numberOfParticles;
    Double length;
    Double radiusLimit;
    Double speed;
    Double weight;

    public InputGenerator(Integer numberOfParticles, Double length, Double radiusLimit, Double speed, Double weight) {
        this.numberOfParticles = numberOfParticles;
        this.length = length;
        this.radiusLimit = radiusLimit;
        this.speed = speed;
        this.weight = weight;
    }

    public static void main(String[] args) throws IOException {
        new InputGenerator(100, 20.0, 0.0015, 0.001, 1.0).generate() ;
    }
    public void generate() throws IOException {
        this.staticFile();
        this.dynamicFile();
    }

    public void staticFile() throws IOException {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(numberOfParticles.toString());
        stringBuilder.append("\n");
        stringBuilder.append(length);
        stringBuilder.append("\n");
        for(int i = 0; i < numberOfParticles; i++) {
            stringBuilder.append(radiusLimit);
            stringBuilder.append(" ");
            stringBuilder.append(weight);
            stringBuilder.append("\n");
        }
        File file = new File("./resources/staticFile");
        FileWriter fileWriter = new FileWriter("./staticFile");
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();
    }

    public void dynamicFile() throws IOException {
        Double auxAngle;
        StringBuilder stringBuilder = new StringBuilder("t0\n");

        for(int i = 0; i < numberOfParticles; i++) {
            stringBuilder.append(Math.random()*length);
            stringBuilder.append(" ");
            stringBuilder.append(Math.random()*length);
            stringBuilder.append(" ");
            auxAngle = Math.random()*2*Math.PI;
            stringBuilder.append(Math.cos(auxAngle)*speed);
            stringBuilder.append(" ");
            stringBuilder.append(Math.sin(auxAngle)*speed);

            stringBuilder.append("\n");
        }
        File file = new File("./resources/dynamicFile");
        FileWriter fileWriter = new FileWriter("./dynamicFile");
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();
    }

}
