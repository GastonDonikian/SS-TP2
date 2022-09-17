package sistema;

import models.*;
import servicios.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDrivenParticles {
    List<Particle> particleList;
    double EPSILON = 0.01;
    Particle particle1;
    Particle particle2;
    Particle wallParticle;
    Boolean wallCrash = false;
    Boolean isVertical = false;
    List<List<Particle>> particlesThroughTime = new ArrayList<>();

    public void runSimulation() throws IOException {
        InputParser inputParser = new InputParser();
        this.particleList = inputParser.getParticleList();
        double time;
        saveState(particleList);
        for(int i = 0; i < 100; i++) {
            time = calculateMinCrashTime(particleList);
            evolveAllParticles(time);
            saveState(particleList);
            crashParticles();
        }
        String outputString = getOutputString();
        FileWriter fileWriter = new FileWriter("./plotter/Output");
        fileWriter.write(outputString);
        fileWriter.close();
    }

    private void saveState(List<Particle> particles) {
        List<Particle> auxList = new ArrayList<>();
        particles.forEach(particle -> auxList.add(new Particle(particle.getX(), particle.getY(), particle.getRadius(), particle.getId(), particle.getAngle(), particle.getWeight())));
        particlesThroughTime.add(auxList);
    }

    private String getOutputString() {
        StringBuilder s = new StringBuilder();
        List<Particle> currentParticles;
        Integer particleSize = particlesThroughTime.get(0).size();
        for (int i = 0; i < particlesThroughTime.size(); i++) {
            currentParticles = particlesThroughTime.get(i);
            s.append(particleSize).append("\n");
            s.append("t").append(i).append("\n");
            for (Particle particle : currentParticles)
                s.append(particle.getX()).append(" ").append(particle.getY()).append(" ").append(particle.getXSpeed()).append(" ").append(particle.getYSpeed()).append("\n");
        }
        return s.toString();
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

    public void crashParticles(){
        if ( wallCrash ){
            crashWallParticles();
        }
        else {
            crashTwoParticles();
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

    public void crashTwoParticles(){
        double delta_r_x = particle2.getX() - particle1.getX();
        double delta_r_y = particle2.getY() - particle1.getY();
        double delta_v_x = particle2.getXSpeed() - particle1.getXSpeed();
        double delta_v_y = particle2.getYSpeed() - particle1.getYSpeed();
        double delta_v_r = delta_v_x*delta_r_x + delta_v_y*delta_r_y;
        double sigma = particle2.getRadius() + particle1.getRadius();
        double j = (2*particle1.getWeight()*particle2.getWeight()*delta_v_r)/(sigma*(particle1.getWeight()+particle2.getWeight()));
        double j_x = j*delta_r_x/sigma;
        double j_y = j*delta_r_y/sigma;
        double p1_xSpeed = particle1.getXSpeed() + j_x/particle1.getWeight();
        double p1_ySpeed = particle1.getYSpeed() + j_y/particle1.getWeight();
        double p2_xSpeed = particle2.getXSpeed() + j_x/particle2.getWeight();
        double p2_ySpeed = particle2.getYSpeed() + j_y/particle2.getWeight();
        particle1.setXSpeed(p1_xSpeed);
        particle1.setYSpeed(p1_ySpeed);
        particle2.setXSpeed(p2_xSpeed);
        particle2.setYSpeed(p2_ySpeed);
    }

    public void evolveAllParticles(Double time) {
        for(Particle particle : particleList) {
            particle.setCoordinates(particle.getX() + time * particle.getXSpeed(),particle.getY() + time * particle.getYSpeed());
        }
    }

    public double calculateMinCrashTime(List<Particle> particleList){
        double minTime = Double.MAX_VALUE;
        for (Particle particle: particleList){
            double wallCrashTime = getParticleWallCrashTime(particle);
            double particleCrashTime = getParticleCrashTime(particle, particleList, minTime);

            if ( particleCrashTime < minTime && particleCrashTime >= 0){
                minTime = particleCrashTime;
                wallCrash = false;
            }
            if ( wallCrashTime < minTime && wallCrashTime >= 0){
                minTime = wallCrashTime;
                wallParticle = particle;
                wallCrash = true;
            }

        }

        return minTime;
    }

    public double getParticleWallCrashTime(Particle particle){
        double leftWallX = 0;
        double rightWallX = 0.24;
        double middleWallX = 0.12;
        double bottomWallY = 0;
        double topWallY = 0.09;
        double cavitySize = 0.01;
        double bottomSpaceY = (topWallY/2) - (cavitySize/2);
        double topSpaceY = (topWallY/2) + (cavitySize/2);

        double yCrashTime = Double.MAX_VALUE;
        double xCrashTime = Double.MAX_VALUE;

        //Chequeo en Y
        if ( particle.getYSpeed() > 0){
            yCrashTime = (topWallY - (particle.getY() + particle.getRadius()) )/particle.getYSpeed();
        }
        else if (particle.getYSpeed() < 0){
            yCrashTime = (particle.getY() - particle.getRadius())/particle.getYSpeed();
        }

        //Chequeo en X
        if ( particle.getXSpeed() > 0){
            if ( ((particle.getY() + particle.getRadius()) >= topSpaceY || (particle.getY() - particle.getRadius()) <= bottomSpaceY) && particle.getX() < middleWallX){
                xCrashTime = (middleWallX - (particle.getX() + particle.getRadius()) )/particle.getXSpeed();
            }
            else {
                xCrashTime = (rightWallX - (particle.getX() + particle.getRadius()) )/particle.getXSpeed();
            }
        }
        else if ( particle.getXSpeed() < 0){
            if ( ((particle.getY() + particle.getRadius()) < topSpaceY && (particle.getY() - particle.getRadius()) > bottomSpaceY) || particle.getX() < middleWallX){
                //pasa por la rendija
                xCrashTime = (particle.getX() - particle.getRadius())/particle.getXSpeed();
            }
            else {
                xCrashTime = (particle.getX() - particle.getRadius() - middleWallX )/particle.getXSpeed();
            }
        }

        if ( xCrashTime < yCrashTime){
            isVertical = false;
            return xCrashTime;
        }
        else {
            isVertical = true;
            return yCrashTime;
        }
    }

    public double getParticleCrashTime(Particle particle, List<Particle> particles, Double minTime){
        Double minTimeAux = Double.MAX_VALUE;
        for(Particle p: particles) {
            if (p == particle)
                continue;
            Double time = calculateCrashTime(p,particle);
            if (time != null && time < minTimeAux){
                minTimeAux = time;
                if ( minTimeAux < minTime){
                    this.particle1 = p;
                    this.particle2 = particle;
                }
            }
        }
        return minTimeAux;
    }
}
