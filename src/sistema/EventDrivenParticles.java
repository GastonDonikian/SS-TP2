package sistema;

import models.*;
import servicios.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EventDrivenParticles extends main {
    List<Particle> particleList;
    List<List<Particle>> particlesThroughTime = new ArrayList<>();
    List<Double> timeThroughEvents = new ArrayList<>();
    Double x;
    Double y;
    Double cavitySize;
    Double globalTime = 0.0;

    int progress = 0;


    Particle particle1;
    Particle particle2;
    Particle wallParticle;
    Boolean wallCrash = false;
    Boolean isVertical = false;
    Double timeInterval = null;
    Integer eventInterval = null;
    Integer i;

    double delta_t = 2000;

    boolean isFinished = false;

    Double lastTimeSaved = 0.0;
    Integer lastEvent = 0;

    public EventDrivenParticles(Double x, Double y, Double cavitySize,Double timeInterval) {
        this.x = x;
        this.y = y;
        this.cavitySize = cavitySize;
        this.timeInterval = timeInterval;
    }

    public EventDrivenParticles(Double x, Double y, Double cavitySize,Integer eventInterval,Integer i) {
        this.x = x;
        this.y = y;
        this.cavitySize = cavitySize;
        this.eventInterval = eventInterval;
        this.i = i;
    }

    public void runSimulation() throws IOException {
        InputParser inputParser = new InputParser();
        this.particleList = inputParser.getParticleList();

        FileWriter fileWriter = new FileWriter("./resources/time_vs_slot/m=0.04/outputFile" + i);
        double evolveTime;
        saveState(particleList);
        int i = 0;
        boolean writing = true;
        double cutTime = 0;
        double totalImpulse = 0;

        while (!isFinished){
            i++;
            evolveTime = calculateMinCrashTime(particleList);
            globalTime += evolveTime;
            evolveAllParticles(evolveTime);
            saveState(particleList);
            crashParticles();
            if (writing && canRun()) {
                if (i % 10000 == 0 && i > 0) {
                    getOutputString(fileWriter);
                    particlesThroughTime = new ArrayList<>();
                }
            } else if (writing) {
                getOutputString(fileWriter);
                writing = false;
                cutTime = globalTime;
            } else {
                if (globalTime >= cutTime+delta_t) {
                    isFinished = true;
                } else if (wallCrash) {
                    if (isVertical) {
                        totalImpulse += wallParticle.getWeight()*Math.abs(wallParticle.getYSpeed())*2;
                    } else {
                        totalImpulse += wallParticle.getWeight()*Math.abs(wallParticle.getXSpeed())*2;
                    }
                }
            }
        }
        fileWriter.close();

        double perimeter = 4*y + 2*x - 2*cavitySize;
        double totalPressure = totalImpulse/(delta_t*perimeter);
        double temperature = particleList.get(0).getWeight()*(Math.pow(particleList.get(0).getXSpeed(), 2)+Math.pow(particleList.get(0).getYSpeed(), 2))/2;
        temperature = Math.pow(0.01, 2)/2;
    }

    private boolean canRun(){
        int leftSide = 0;
        int rightSide = 0;
        double epsilon = 0.05;

        for (Particle particle : particleList) {
            if (particle.getX() < x/2)
                leftSide++;
            else if (particle.getX() > x/2)
                rightSide++;
        }
        return Math.abs(((double) leftSide / (leftSide + rightSide)) - 0.5) > epsilon;
    }

    private void saveState(List<Particle> particles) {

        if( (this.eventInterval == null) && (lastTimeSaved + timeInterval > globalTime) ||
                ((this.timeInterval == null) && (lastEvent++ % eventInterval != 0)))
            return;
        lastTimeSaved = globalTime;
        List<Particle> auxList = new ArrayList<>();
        for (Particle particle : particles) {
            Particle auxParticle = new Particle(particle.getX(), particle.getY(), particle.getRadius(), particle.getId(), particle.getAngle(), particle.getWeight());
            auxParticle.setXSpeed(particle.getXSpeed());
            auxParticle.setYSpeed(particle.getYSpeed());
            auxList.add(auxParticle);
        }
        timeThroughEvents.add(globalTime);
        particlesThroughTime.add(auxList);
    }

    private void getOutputString(FileWriter fileWriter) throws IOException {
        StringBuilder s = new StringBuilder();
        List<Particle> currentParticles;
        Integer particleSize = particlesThroughTime.get(0).size();
        for (List<Particle> particles : particlesThroughTime) {
            currentParticles = particles;
            s.append(particleSize).append("\n");
            s.append("t ").append(timeThroughEvents.get(progress).toString()).append(" event ").append(progress).append("\n");
            for (Particle particle : currentParticles)
                s.append(particle.getX()).append(" ").append(particle.getY()).append("\n");
            progress++;
        }
        fileWriter.write(s.toString());
    }

    private Double calculateCrashTime(Particle particle1, Particle particle2) {
        double delta_r_x = particle2.getX() - particle1.getX();
        double delta_r_y = particle2.getY() - particle1.getY();
        double delta_v_x = particle2.getXSpeed() - particle1.getXSpeed();
        double delta_v_y = particle2.getYSpeed() - particle1.getYSpeed();
        double r_squared = Math.pow(delta_r_x,2) + Math.pow(delta_r_y,2);
        double v_squared = Math.pow(delta_v_x,2) + Math.pow(delta_v_y,2);
        double delta_v_r = delta_v_x*delta_r_x + delta_v_y*delta_r_y;
        double sigma =  particle2.getRadius() + particle1.getRadius();
        double d = Math.pow(delta_v_r,2) - v_squared * (r_squared - Math.pow(sigma,2));
        if(delta_v_r >= 0 || d < 0)
            return null;
        return - (delta_v_r + Math.sqrt(d))/v_squared;
    }

    /*
    public double calculateNextParticleCrash(List<Particle> particleList) {
        Double minTime = Double.MAX_VALUE;
        Double aux;
        for(Particle particle1 : particleList) {
            for(Particle particle2: particleList) {
                if (particle1 == particle2)
                    continue;
                aux = calculateCrashTime(particle1,particle2);
                if(aux != null && minTime - aux < EPSILON) {
                    minTime = aux;
                    this.particle1 = particle1;
                    this.particle2 = particle2;
                }
            }
        }
        return minTime;
    }
*/

    public void crashParticles(){
        if ( wallCrash ){
            crashWallParticles();
        }
        else {
            crashTwoParticles(particle1, particle2);
        }
    }

    public void crashWallParticles(){
        if ( isVertical ){
            wallParticle.setYSpeed(wallParticle.getYSpeed() * -1);
        }
        else {
            wallParticle.setXSpeed(wallParticle.getXSpeed() * -1);
        }
    }

    public void crashTwoParticles(Particle particle1, Particle particle2){
        double delta_r_x = particle2.getX() - particle1.getX();
        double delta_r_y = particle2.getY() - particle1.getY();
        double delta_v_x = particle2.getXSpeed() - particle1.getXSpeed();
        double delta_v_y = particle2.getYSpeed() - particle1.getYSpeed();
        double delta_v_r = delta_v_x*delta_r_x + delta_v_y*delta_r_y;
        double sigma = particle2.getRadius() + particle1.getRadius();
        double j = (2.0*particle1.getWeight()*particle2.getWeight()*delta_v_r)/(sigma*(particle1.getWeight()+particle2.getWeight()));
        double j_x = j*delta_r_x/sigma;
        double j_y = j*delta_r_y/sigma;
        double p1_xSpeed = particle1.getXSpeed() + j_x/particle1.getWeight();
        double p1_ySpeed = particle1.getYSpeed() + j_y/particle1.getWeight();
        double p2_xSpeed = particle2.getXSpeed() - j_x/particle2.getWeight();
        double p2_ySpeed = particle2.getYSpeed() - j_y/particle2.getWeight();
        double a = Math.pow(particle1.getX() - particle2.getX(), 2) + Math.pow(particle1.getY() - particle2.getY(), 2);
        double b = Math.pow(particle1.getRadius() + particle2.getRadius(), 2);
//        if (Math.abs(a - b) > 0.01) {
//            System.out.println(a + " == " + b);
//            throw new IllegalStateException();
//        }
        double p1_v = Math.sqrt(Math.pow(particle1.getXSpeed(), 2) + Math.pow(particle1.getYSpeed(), 2));
//        if ( Math.pow(p1_xSpeed, 2) + Math.pow(p1_ySpeed, 2) > 0.0002 || Math.pow(p2_xSpeed, 2) + Math.pow(p2_ySpeed, 2) > 0.0002){
//            System.out.println("P1x: "+  particle1.getX() + " P1y: " + particle1.getY() + "xspeed: " + particle1.getXSpeed() + " yspeed: " + particle1.getYSpeed());
//            System.out.println("P2x: "+  particle2.getX() + " P2y: " + particle2.getY() + "xspeed: " + particle2.getXSpeed() + " yspeed: " + particle2.getYSpeed());
//            System.out.println("JX: " + j_x + " JY: " + j_y + " J: " + j);
//            System.out.println("P1xSpeed: " + p1_xSpeed + " P1ySpeed: " + p1_ySpeed + " P2xSpeed: " + p2_xSpeed + " P2ySpeed: " + p2_ySpeed);
//
//            //double a = Math.pow(particle1.getX() - particle2.getX(), 2) + Math.pow(particle1.getY() - particle2.getY(), 2);
//            //double b = Math.pow(particle1.getRadius() + particle2.getRadius(), 2);
//            System.out.println(" segundo" + a + "==" + b);
//            throw new IllegalStateException();
//        }
        particle1.setXSpeed(p1_xSpeed);
        particle1.setYSpeed(p1_ySpeed);
        particle2.setXSpeed(p2_xSpeed);
        particle2.setYSpeed(p2_ySpeed);
    }

    public void evolveAllParticles(Double time) {
        for(Particle particle : particleList) {
            double nextX = particle.getX() + time * particle.getXSpeed();
            double nextY = particle.getY() + time * particle.getYSpeed();
            if ( nextX < 0 || nextX > x || nextY < 0 || nextY > y){
                System.out.println(particle);
                throw new IllegalStateException();
            }
            particle.setCoordinates(nextX, nextY);
        }
    }

    public double calculateMinCrashTime(List<Particle> particleList){
        double minTime = Double.MAX_VALUE;
        for (Particle particle: particleList){
            double wallCrashTime = getParticleWallCrashTime(particle, minTime);
            double particleCrashTime = getParticleCrashTime(particle, particleList, minTime);

            //System.out.println("WallcrashTime: " + wallCrashTime + " ParticleCrashTime: " + particleCrashTime);
            if ( particleCrashTime < minTime){
                if ( particleCrashTime < 0){
                    System.out.println("CALCULATEMINCRASHTIMEERROR -> Particle");
                    throw new IllegalStateException();
                }
                minTime = particleCrashTime;
                wallCrash = false;
            }
            if ( wallCrashTime < minTime){
                if ( wallCrashTime < 0){
                    System.out.println("CALCULATEMINCRASHTIMEERROR -> Wall");
                    throw new IllegalStateException();
                }
                minTime = wallCrashTime;
                wallCrash = true;
                wallParticle = particle;
            }

        }

        return minTime;
    }

    public double getParticleWallCrashTime(Particle particle, double minTime){
        double leftWallX = 0;
        double rightWallX = x;
        double middleWallX = x/2;
        double bottomWallY = 0;
        double topWallY = y;
        double bottomSpaceY = (topWallY/2) - (cavitySize/2);
        double topSpaceY = (topWallY/2) + (cavitySize/2);

        double yCrashTime = Double.MAX_VALUE;
        double xCrashTime = Double.MAX_VALUE;

        //Chequeo en Y
        if ( particle.getYSpeed() > 0){
            yCrashTime = (topWallY - (particle.getY() + particle.getRadius()) )/particle.getYSpeed();
        }
        else if (particle.getYSpeed() < 0){
            yCrashTime = (bottomWallY - (particle.getY() - particle.getRadius()))/particle.getYSpeed();
        }

        //Chequeo en X
        if ( particle.getXSpeed() > 0){
            if ( particle.getX() < middleWallX ){
                xCrashTime = (middleWallX - (particle.getX() + particle.getRadius()) )/particle.getXSpeed();
                double futureYPosition = particle.getY() + particle.getYSpeed() * xCrashTime;
                if ( (futureYPosition + particle.getRadius()) < topSpaceY && (futureYPosition - particle.getRadius()) > bottomSpaceY){
                    xCrashTime = (rightWallX - (particle.getX() + particle.getRadius()) )/particle.getXSpeed();
                }
                else if ( xCrashTime < 0 ){
                    xCrashTime = (rightWallX - (particle.getX() + particle.getRadius()) )/particle.getXSpeed();
                }
            }
            else {
                xCrashTime = (rightWallX - (particle.getX() + particle.getRadius()) )/particle.getXSpeed();
            }
        }
        else if ( particle.getXSpeed() < 0){
            if ( particle.getX() > middleWallX){
                xCrashTime = (middleWallX - (particle.getX() - particle.getRadius()) )/particle.getXSpeed();
                double futureYPosition = particle.getY() + particle.getYSpeed() * xCrashTime;
                if ( (futureYPosition + particle.getRadius()) < topSpaceY && (futureYPosition - particle.getRadius()) > bottomSpaceY){
                    xCrashTime = (leftWallX - (particle.getX() - particle.getRadius()) )/particle.getXSpeed();
                }
                else if ( xCrashTime < 0 ){
                    xCrashTime = (leftWallX - (particle.getX() - particle.getRadius()) )/particle.getXSpeed();
                }
            }
            else {
                xCrashTime = (leftWallX - (particle.getX() - particle.getRadius()) )/particle.getXSpeed();
            }
        }

        if ( xCrashTime < yCrashTime){
            if ( xCrashTime < 0){
                System.out.println("XCRASHTIMEERROR");
                throw new IllegalStateException();
            }
            if ( xCrashTime < minTime)
                isVertical = false;
            return xCrashTime;
        }
        else {
            if (yCrashTime < 0){
                System.out.println("YCRASHTIMEERROR");
                throw new IllegalStateException();
            }
            if ( yCrashTime < minTime)
                isVertical = true;
            return yCrashTime;
        }
    }

    public double getParticleCrashTime(Particle particle, List<Particle> particles, double minTime){
        double minTimeAux = Double.MAX_VALUE;
        for(Particle p: particles) {
            if (p.getId() != particle.getId()){
                Double time = calculateCrashTime(particle,p);
                if (time != null && time < minTime && time < minTimeAux){
                    if ( time < 0){
                        System.out.println("Superpuestas: ERROR");
                        System.out.println(particle);
                        System.out.println(p);

                        double a = Math.pow(particle.getX() - p.getX(), 2) + Math.pow(particle.getY() - p.getY(), 2);
                        double b = Math.pow(particle.getRadius() + p.getRadius(), 2);
                        System.out.println(a + " == " + b);
                        throw new IllegalStateException();
                    }
                    particle1 = particle;
                    particle2 = p;
                    minTimeAux = time;
                }
            }
        }
        return minTimeAux;
    }
}
