package sistema;

import models.Particle;
import servicios.InputParser;

import java.util.List;
import java.util.Map;

public class EventDrivenParticles {
    List<Particle> particleList;
    double EPSILON = 0.01;
    Particle particle1;
    Particle particle2;

    public void runSimulation() {
        InputParser inputParser = new InputParser();
        this.particleList = inputParser.getParticleList();
        double time;
        for(int i = 0; i < 100; i++) {
            time = calculateNextParticleCrash(particleList);
            printToOutput(particleList);
            evolveAllParticles(time);

        }
    }
    private void printToOutput(List<Particle> particleList) {
        System.out.println("Hola");
    }
    private Double calculateCrash(Particle particle1,Particle particle2) {
        ///TODO: LEAN EL PPT: SLIDE 14 Y CONFIRMEN QUE ESTO ESTE BIEN, LOQUITAS
        //TODO: FALTA TIEMPO DE CHOQUE CON PAREDES
        double delta_r_x = particle2.getX() - particle1.getX();
        double delta_r_y = particle2.getY() - particle1.getY();
        double delta_v_x = particle2.getXSpeed() - particle1.getXSpeed();
        double delta_v_y = particle2.getYSpeed() - particle1.getYSpeed();
        double r_squared = Math.pow(delta_r_x,2) + Math.pow(delta_r_y,2);
        double v_squared = Math.pow(delta_v_x,2) + Math.pow(delta_v_y,2);
        double delta_v_r = delta_v_x*delta_r_x + delta_v_y*delta_r_y;
        //NO SE SI ACA TENDRIA QUE TENER EN CUENTA EL RADIO, PROBABLEMNENTE
        double d = Math.pow(delta_v_r,2) - v_squared * (r_squared - Math.pow(delta_r_x + delta_r_y,2));
        if(delta_v_r >= 0 || d < 0)
            return null;
        return - (delta_v_r + Math.sqrt(d))/v_squared;
        /*      double xDifference = particle1.getX() - particle2.getX();
        double yDifference = particle1.getY() - particle2.getY();

        double xSpeedDifference = particle1.getXSpeed() - particle2.getXSpeed();
        double ySpeedDifference = particle1.getYSpeed() - particle2.getYSpeed();

        double xTimeOfCrash = xDifference / (-1.0 * xSpeedDifference);
        double yTimeOfCrash = yDifference / (-1.0 * ySpeedDifference);

        return Math.abs(xTimeOfCrash - yTimeOfCrash) < EPSILON ? xTimeOfCrash : null;
 */
    }

    public double calculateNextParticleCrash(List<Particle> particleList) {
        Double minTime = Double.MAX_VALUE;
        Double aux;
        for(Particle particle1 : particleList) {
            for(Particle particle2: particleList) {
                if (particle1 == particle2)
                    continue;
                aux = calculateCrash(particle1,particle2);
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

    }

    public void evolveAllParticles(Double time) {
        for(Particle particle : particleList) {
            particle.setCoordinates(particle.getX() + time * particle.getXSpeed(),particle.getY() + time * particle.getYSpeed());
        }
    }
}
