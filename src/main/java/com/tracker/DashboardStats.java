package com.tracker;

/**
 * Represents task summary statistics for a dashboard.
 * Stores the total number of tasks and the number of tasks
 * in each priority category.
 *
 * @author David Renteria
 * @version 0.1.0
 * @since 4/13/2026
 */
public class DashboardStats {

    private final int total;
    private final int urgent;
    private final int high;
    private final int medium;
    private final int low;

    /**
     * Constructs a DashboardStats object with the specified counts.
     *
     * @param total the total number of tasks
     * @param urgent the number of urgent tasks
     * @param high the number of high-priority tasks
     * @param medium the number of medium-priority tasks
     * @param low the number of low-priority tasks
     */
    public DashboardStats(int total, int urgent, int high, int medium, int low) {
        this.total = total;
        this.urgent = urgent;
        this.high = high;
        this.medium = medium;
        this.low = low;
    }

    public int getTotal() {
        return total;
    }

    public int getUrgent() {
        return urgent;
    }

    public int getHigh() {
        return high;
    }

    public int getMedium() {
        return medium;
    }

    public int getLow() {
        return low;
    }
}