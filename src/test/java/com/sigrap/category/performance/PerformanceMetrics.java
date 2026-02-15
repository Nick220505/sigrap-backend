package com.sigrap.category.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to collect and calculate performance metrics.
 */
class PerformanceMetrics {
    private final String name;
    private List<Long> durations = new ArrayList<>();
    private long totalDuration;
    private long totalOperations;

    public PerformanceMetrics(String name) {
        this.name = name;
    }

    public void recordDurations(List<Long> durations) {
        this.durations = new ArrayList<>(durations);
    }

    public void recordConcurrentTest(long duration, long operations) {
        this.totalDuration = duration;
        this.totalOperations = operations;
    }

    public boolean hasData() {
        return !durations.isEmpty() || totalOperations > 0;
    }

    public double getAverage() {
        if (durations.isEmpty()) return 0;
        return durations.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public long getP50() {
        return getPercentile(50);
    }

    public long getP95() {
        return getPercentile(95);
    }

    public long getP99() {
        return getPercentile(99);
    }

    public long getMin() {
        if (durations.isEmpty()) return 0;
        return Collections.min(durations);
    }

    public long getMax() {
        if (durations.isEmpty()) return 0;
        return Collections.max(durations);
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public double getThroughput() {
        if (totalDuration == 0) return 0;
        return (totalOperations * 1000.0) / totalDuration;
    }

    private long getPercentile(int percentile) {
        if (durations.isEmpty()) return 0;
        List<Long> sorted = new ArrayList<>(durations);
        Collections.sort(sorted);
        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }

    public void reset() {
        durations = new ArrayList<>();
        totalDuration = 0;
        totalOperations = 0;
    }

    public String getName() {
        return name;
    }
}
