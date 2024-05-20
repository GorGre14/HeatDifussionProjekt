package com.heatDifussion.myapp;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class ComputationWorker extends Thread {

    private final int start;
    private final int end;
    private final int width;
    private final int height;
    private final double[][] temperature;
    private final CyclicBarrier barrier;

    public ComputationWorker(int start, int end, int width, int height, double[][] temperature, CyclicBarrier barrier) {
        this.start = start;
        this.end = end;
        this.width = width;
        this.height = height;
        this.temperature = temperature;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (int i = start; i < end; i++) {
                for (int j = 0; j < width; j++) {
                    if (temperature[i][j] != 1) {
                        double up = 0, down = 0, left = 0, right = 0;
                        if (i > 0) up = temperature[i - 1][j];
                        if (i < height - 1) down = temperature[i + 1][j];
                        if (j > 0) left = temperature[i][j - 1];
                        if (j < width - 1) right = temperature[i][j + 1];
                        temperature[i][j] = (up + down + left + right) / 4;
                    }
                }
            }
            if (barrier != null) {
                try {
                    barrier.await(); // Wait for other threads to reach this point
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                }
            }
        }
    }
}
