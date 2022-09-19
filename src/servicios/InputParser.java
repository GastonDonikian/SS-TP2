package servicios;


import models.Particle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class InputParser extends main {
    Integer numberOfParticles;
    Double x;
    Double y;
    Double radius;
    private final List<Particle> particleList = new ArrayList<>();

    public InputParser() throws FileNotFoundException {
        this.parseStatic();
        this.parseDynamic("t0");
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

    private void parseStatic() throws FileNotFoundException {
        File staticFile = new File("./resources/staticFile");
        Scanner scanner = new Scanner(staticFile).useLocale(Locale.ENGLISH); //Cambiar al file que sea...
        numberOfParticles = scanner.nextInt();
        x = scanner.nextDouble();
        y = scanner.nextDouble();
        radius = scanner.nextDouble();
        for (int i = 0; i < numberOfParticles; i++) {
            particleList.add(new Particle(scanner.nextDouble(), scanner.nextDouble(),i+1));
        }
    }

    private void parseDynamic(String timeNum) throws FileNotFoundException {
        File dynamicFile = new File("./resources/dynamicFile");
        Scanner scanner = new Scanner(dynamicFile).useLocale(Locale.ENGLISH); //Cambiar al file que sea...
        scanner.skip(timeNum);
        for (Integer i = 0; i < numberOfParticles; i++) {
            double xDist = scanner.nextDouble();
            double yDist = scanner.nextDouble();
            double xSpeed = scanner.nextDouble();
            double ySpeed = scanner.nextDouble();
            particleList.get(i).setCoordinates(xDist, yDist);
            particleList.get(i).calculateAngle(xSpeed, ySpeed);
        }
    }
}
