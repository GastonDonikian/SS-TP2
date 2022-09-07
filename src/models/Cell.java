package models;

import sistema.ICM;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final List<Particle> particleList = new ArrayList<>();
    private final List<Particle> cellNeighbors = new ArrayList<>();

    public Cell(List<Particle> particleList) {
        this.particleList.addAll(particleList);
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

    public List<Particle> getCellNeighbors() {
        return cellNeighbors;
    }

    public void addParticle(Particle particle) {
        particleList.add(particle);
    }

}
