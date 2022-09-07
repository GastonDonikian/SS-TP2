package sistema;

import models.Cell;
import models.Particle;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ICM {
    //Estuve leyendo un poco el TP, este es el input:
    /*
 las posiciones y radios de
N partículas y los parámetros N, L, M y rc (ver punto 5). Las N partículas deben ser generadas en
forma aleatoria dentro del área de lado L

       ALGORITMO EN PAPEL;
            1.Recibo particulas.
            2.Por cada celda cargo las que entran en esa celda, cada celda es una lista de particulas. Una particula puede estar en mas de una celda.
            3.Hago una martiz de NxN de ceros, donde hay una particula adyacente a otro le pongo un 1. Esta matriz no representa la matriz de celdas, es solo
                una celda de ids de particulas.
            4. Recorro la matriz y muestro el output pedido.

    OUTPUT: [id de la partícula "i" id's de las partículas cuya distancia borde-borde es menos de rc].ESTO PARA CADA PARTICULA.

     */
    private final Integer numberOfParticles; //N
    private final Double length; //L
    private Integer rowNumber; //M numbero de celdas, o sea que L/M (la long. de una celda) tiene que ser mayor a r = Rc + R part
    private Double cellLength;
    private final Double radiusC;
    private final Boolean isPeriodic;
    private List<Particle> particleList = new ArrayList<>();
    List<Point> stepList = new ArrayList<>();

//    private final List<List<Cell>> matrix = new ArrayList<>();
    private Cell[][] matrix;
    private Map<Particle, List<Particle>> neighbors = new HashMap<>();

    public ICM(Integer N,Double L, Double radiusC, boolean isPeriodic) {
        numberOfParticles = N;
        length = L;
        this.radiusC = radiusC;
        this.isPeriodic = isPeriodic;
        int[][] steps = {{0,-1}, {1,-1}, {1,0}, {1,1}};

        for (int[] step : steps) {
            stepList.add(new Point(step[0], step[1]));
        }
    }

    public Map<Particle, List<Particle>> runTesting(List<Particle> particleList){
        List<Particle> newList = particleList;
        if(particleList == null || particleList.isEmpty()){
            newList = generateParticles();
        }

        if ( rowNumber == null)
            rowNumber = this.calculateRowNumber(newList);

        cellLength = length.doubleValue() / rowNumber;
        matrix = new Cell[rowNumber][rowNumber];

        setMatrix(newList);

        return neighbors;
    }

    public int calculateRowNumber(List<Particle> particleList){
        double maxRadius = 0.0;
        for (Particle particle : particleList) {
            if ( particle.getRadius() > maxRadius)
                maxRadius = particle.getRadius();
        }

        double interactionRadius = this.radiusC + 2 * maxRadius;
        return (int) (length / interactionRadius);
    }

    private void calculateNeighbours() {
        for ( int i = 0; i < rowNumber; i++){
            for ( int j = 0; j < rowNumber; j++){
                if (matrix[i][j] != null){
                    Cell cell = matrix[i][j];
                    if ( cell.getParticleList().size() > 0){
                        List<Particle> cellNeighbors = getCellNeighbors(j, i);
                        cell.getCellNeighbors().addAll(cellNeighbors);
                    }
                }
            }
        }

        particleList.forEach(particle -> {
            neighbors.putIfAbsent(particle, new ArrayList<>());
            int posX = (int) (particle.getX() / cellLength);
            int posY = (int) (particle.getY() / cellLength);

            List<Particle> cellNeighbors = matrix[posY][posX].getCellNeighbors();
            List<Particle> cellList =  matrix[posY][posX].getParticleList().stream().filter(p -> p.getId() != particle.getId()).collect(Collectors.toList());
            List<Particle> possibleNeighbors = new ArrayList<>();

            possibleNeighbors.addAll(cellNeighbors);
            possibleNeighbors.addAll(cellList);

            possibleNeighbors.forEach(pNeighbor -> {
                neighbors.putIfAbsent(pNeighbor, new ArrayList<>());
                if ( isNeighbor(particle, pNeighbor) ){
                    List<Particle> neighborsList = neighbors.get(particle);
                    if ( !neighborsList.contains(pNeighbor) )
                        neighbors.get(particle).add(pNeighbor);

                    if ( !neighbors.get(pNeighbor).contains(particle) )
                        neighbors.get(pNeighbor).add(particle);
                }
            });
        });
    }

    List<Particle> getCellNeighbors(int posX, int posY){
        List<Particle> particleList = new ArrayList<>();

        stepList.forEach(pair -> {
            int xPosValidated = getPosition(posX + (int) pair.getX());
            int yPosValidated = getPosition(posY + (int) pair.getY());

            if ( xPosValidated != -1 && yPosValidated != -1
                && matrix[yPosValidated][xPosValidated] != null)
            {
                particleList.addAll(matrix[yPosValidated][xPosValidated].getParticleList());
            }
        });

        return particleList;
    }

    int getPosition(int pos){
        if ( pos >= 0 && pos < rowNumber){
            return pos;
        }
        else {
            if ( !isPeriodic){
                return -1;
            }
            else if (pos < 0){
                return pos + rowNumber;
            }
            else {
                return pos % rowNumber;
            }
        }

        /*if ( (pos < 0 || pos > (length / cellLength) - 1) && !this.isPeriodic)
            return -1;

        if ( pos < 0)
            return pos + (length / cellLength);

        return pos % (length / cellLength);
         */
    }

    boolean isNeighbor(Particle p1, Particle p2){
        double xDist = Math.abs(p1.getX() - p2.getX());
        double yDist = Math.abs(p1.getY() - p2.getY());

        if ( isPeriodic ){
            double x2Dist = p1.getX() + (length - p2.getX());
            double x3Dist = p2.getX() + (length - p1.getX());
            xDist = Math.min(xDist, Math.min(x2Dist, x3Dist));

            double y2Dist = p1.getY() + (length - p2.getY());
            double y3Dist = p2.getY() + (length - p1.getY());
            yDist = Math.min(yDist, Math.min(y2Dist, y3Dist));
        }

        double diagonalDist = Math.sqrt( Math.pow(xDist, 2) + Math.pow(yDist, 2) );

        return diagonalDist <= radiusC + p1.getRadius() + p2.getRadius();
    }

    void setMatrix(List<Particle> particleList) {
        //Carga los datos en la matriz
        this.particleList = particleList;
        for(Particle particle : particleList) {
            addParticleToMatrix(particle);
        }
        calculateNeighbours();
    }

    private void addParticleToMatrix(Particle particle) {
        int xPosition = (int) (particle.getX() / cellLength);
        int yPosition = (int) (particle.getY() / cellLength);

        if(matrix[yPosition][xPosition] == null)
            matrix[yPosition][xPosition] = new Cell(new ArrayList<>());
        matrix[yPosition][xPosition].addParticle(particle);
    }

    private List<Particle> generateParticles(){
        List<Particle> responseList = new ArrayList<>();
        Random rand = new Random();
        for ( int i = 0; i < numberOfParticles; i++){
            responseList.add(new Particle(
                    rand.nextDouble() * length,
                    rand.nextDouble() * length,
                    0.25/*rand.nextDouble() * cellLength / 2*/,
                    i,
                    rand.nextDouble() * 360
            ));
        }
        return responseList;
    }
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public Map<Particle, List<Particle>> bruteForce(List<Particle> part){
        long start = System.currentTimeMillis();

        Map<Particle, List<Particle>> bruteForceNeighbors = new HashMap<>();

        for (Particle particle : part) {
            bruteForceNeighbors.put(particle, new ArrayList<>());

            part.forEach(p -> {
                if ( !particle.equals(p) && isNeighbor(particle, p) ){
                    bruteForceNeighbors.get(particle).add(p);
                }
            });
        }

        long end = System.currentTimeMillis();

        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.print("Brute Force Execution time is " + formatter.format((end - start) / 1000d) + " seconds\n");


        long totalNeighbors = bruteForceNeighbors.keySet().stream().sorted(Comparator.comparing(Particle::getId)).mapToLong(particle -> bruteForceNeighbors.get(particle).size()).sum();
        System.out.println("Brute Force Total Neighbors " + totalNeighbors);

        String output = getICMOutput(bruteForceNeighbors);

        return bruteForceNeighbors;
    }


    public String getICMOutput(Map<Particle, List<Particle>> particleListMap) {
        StringBuilder s = new StringBuilder();
        particleListMap.keySet().stream()
                .sorted(Comparator.comparing(Particle::getId))
                .forEach(particle -> {
                    s.append(particle.getId());
                    particleListMap.get(particle).forEach(neighbor -> {
                        s.append(" ").append(neighbor.getId());
                    });
                    s.append("\n");
                });
        //System.out.println(s);
        return s.toString();
    }

    public void resetICM(){
        this.particleList = new ArrayList<>();
        this.neighbors = new HashMap<>();
    }

    public double getLength() {
        if ( length == null)
            return 0;
        return length;
    }
}
