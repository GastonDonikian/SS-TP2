package servicios;

import sistema.EventDrivenParticles;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        Integer N = 50;
        Double v = 0.01;
        Double x = 0.24;
        Double y = 0.09;
        Double cavitySize = 0.01;
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
        EventDrivenParticles eventDrivenParticlesSimulation = new EventDrivenParticles(x, y, cavitySize);
        eventDrivenParticlesSimulation.runSimulation();
    }
}
