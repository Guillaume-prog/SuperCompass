package dev.regucorp.arrow.smoothing;

public class RollingAverage {
    private int size;
    private double total = 0d;
    private int index = 0;
    private double samples[];
    private boolean isFull = false;

    public RollingAverage(int size) {
        this.size = size;
        samples = new double[size];
        reset();
    }

    public void add(double x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        if (++index == size) {
            index = 0; // cheaper than modulus
            isFull = true;
        }
    }

    public void reset() {
        for (int i = 0; i < size; i++) samples[i] = 0d;
    }

    public double getAverage() {
        return total / ((isFull) ? size : index);
    }
}
