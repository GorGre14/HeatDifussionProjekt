package com.heatDifussion.myapp;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComputationHandler {

    private double[][] temperature;
    private ExecutorService executorService;


    // for sequential program mode
    public ComputationHandler(double[][] temperature) {
        this.temperature = temperature;

        executorService = Executors.newSingleThreadExecutor();
        ComputationWorker computationWorker = new ComputationWorker(
                0,
                temperature.length,
                temperature.length,
                temperature.length,
                temperature
        );
        executorService.submit(computationWorker);
    }


}
