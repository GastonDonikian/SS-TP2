package servicios;

import sistema.EventDrivenParticles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class main {
    private static void makeOvitoCage(Double x, Double y, Double cavitySize) throws IOException {
        Double wallRadius = 0.001;
        int particles = 0;
        FileWriter fileWriter = new FileWriter("./resources/ovitoWallFile");
        StringBuilder ovitoCage = new StringBuilder();
        for(int i = 0; i*wallRadius < x;i++) {
            particles +=2;
            ovitoCage.append(i*wallRadius).append(' ').append("0.0").append('\n');
            ovitoCage.append(i*wallRadius).append(' ').append(y).append('\n');
        }
        for(int i = 0; i*wallRadius < y;i++) {
            ovitoCage.append("0.0").append(' ').append(i*wallRadius).append('\n');
            ovitoCage.append(x).append(' ').append(i*wallRadius).append('\n');
            particles +=2;
            if(Math.abs(i*wallRadius - (y/2)) > cavitySize/2) {
                particles += 1;
                ovitoCage.append(x / 2).append(' ').append(i * wallRadius).append('\n');
            }
        }
        ovitoCage.insert(0, particles + "\n\n");
        fileWriter.write(ovitoCage.toString());
        fileWriter.close();
    }
    public static void main(String[] args) throws IOException {
        for(int i = 0; i < 10;i++){
            runMain(args,i);
            System.out.println(i);
        }
    }
    public static void runMain(String[] args, Integer i) throws IOException {
        Integer N = 100;
        Double v = 0.01;
        Double x = 0.24;
        Double y = 0.09;
        Double cavitySize = 0.04;
        Double radius = 0.0015;
        Double weight = 1.0;
        int argNumber = 0;
        for (String arg : args) {
            switch (arg){
                case "-N": argNumber = 1;break;
                case "-v": argNumber = 2;break;
                case "-x": argNumber = 3;break;
                case "-y": argNumber = 4;break;
                case "-cavitySize": argNumber = 5;break;
                case "-radius": argNumber = 6;break;
                case "-weight": argNumber = 7;break;
                default:
                    switch (argNumber){
                        case 1: N = Integer.parseInt(arg);break;
                        case 2: v = Double.parseDouble(arg);break;
                        case 3: x = Double.parseDouble(arg);break;
                        case 4: y = Double.parseDouble(arg);break;
                        case 5: cavitySize = Double.parseDouble(arg);break;
                        case 6: radius = Double.parseDouble(arg);break;
                        case 7: weight = Double.parseDouble(arg);break;
                        default: throw new Error("Arguments error");
                    }
            }
        }
        InputGenerator inputGenerator = new InputGenerator(N, x, y, cavitySize, radius, v, weight);
        inputGenerator.generate();
        EventDrivenParticles eventDrivenParticlesSimulation = new EventDrivenParticles(x, y, cavitySize,1);
        eventDrivenParticlesSimulation.runSimulation();
        makeOvitoCage(x,y,cavitySize);
        //No la creo cada vez, solo cuando cambien el x, y o cavitySize jeje
    }
}
