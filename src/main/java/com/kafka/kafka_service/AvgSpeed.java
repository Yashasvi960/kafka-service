package com.kafka.kafka_service;

public class AvgSpeed {
    private double sum;

    public double getSum() {
        return sum;
    }

    public void add(double v) { sum += v; count++; }
    public double getAverage() { return count == 0 ? 0.0 : sum / count; }

    public void setSum(double speed) {
        this.sum = speed;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    private long count;
}
