package sistema;

import models.ICM;
import models.Particle;

import java.util.List;
import java.util.Map;

public class EventDrivenParticles {
    List<Particle> particleList;
    ICM icm;
    double EPSILON = 0.01;
    public void runSimulation() {


    }
    private Double calculateCrash(Particle particle1,Particle particle2){
        double xDifference = particle1.getX() - particle2.getX();
        double yDifference = particle1.getY() - particle2.getY();

        double xSpeedDifference = particle1.getXSpeed() - particle2.getXSpeed();
        double ySpeedDifference = particle1.getYSpeed() - particle2.getYSpeed();

        double xTimeOfCrash = xDifference / (-1.0 * xSpeedDifference);
        double yTimeOfCrash = yDifference / (-1.0 * ySpeedDifference);

        return Math.abs(xTimeOfCrash - yTimeOfCrash) < EPSILON ? xTimeOfCrash : null;
    }

    public double calculateNextParticleCrash(List<Particle> particleList) {
        Map<Particle,List<Particle>> particleListMap = icm.getInteractingParticles(particleList);
        Double minTime = Double.MAX_VALUE;
        Double aux;
        for(Particle particle1 : particleListMap.keySet()) {
            for (Particle particle2 : particleListMap.get(particle1)) {
                aux = calculateCrash(particle1, particle2);
                if(aux != null && aux < minTime)
                    minTime = aux;
            }
        }
        return minTime;
    }

    public void moveAndCrashParticlesToTime(Double time) {

    }
}
