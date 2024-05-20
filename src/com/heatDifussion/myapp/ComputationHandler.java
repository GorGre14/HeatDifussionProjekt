package com.heatDifussion.myapp;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComputationHandler {

    private double[][] temperature;
    private ExecutorService executorService;
    private CyclicBarrier barrier;

    // For sequential program mode
    public ComputationHandler(double[][] temperature) {
        this.temperature = temperature;

        executorService = Executors.newSingleThreadExecutor();
        ComputationWorker computationWorker = new ComputationWorker(
                0,
                temperature.length,
                temperature[0].length, // width
                temperature.length,    // height
                temperature,
                null                   // no barrier in sequential mode
        );
        executorService.submit(computationWorker);
    }

    // For parallel program mode
    public ComputationHandler(double[][] temperature, int numThreads) {
        this.temperature = temperature;
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.barrier = new CyclicBarrier(numThreads);

        int rowsPerThread = temperature.length / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int start = i * rowsPerThread;
            int end = (i == numThreads - 1) ? temperature.length : start + rowsPerThread;
            ComputationWorker worker = new ComputationWorker(start, end, temperature[0].length, temperature.length, temperature, barrier);
            executorService.submit(worker);
        }
    }

    public void shutdown() {
        try {
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (barrier != null) {
                barrier.reset(); // Reset the barrier to its initial state
            }
        }
    }
}
