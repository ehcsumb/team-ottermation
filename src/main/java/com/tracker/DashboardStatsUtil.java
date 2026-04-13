package com.tracker;

import java.util.ArrayList;

/**
 * Utility class for calculating dashboard task statistics.
 *
 * @author David Renteria
 * @version 0.1.0
 * @since 4/13/2026
 */
public class DashboardStatsUtil {

    /**
     * Calculates summary statistics for a list of tasks.
     *
     * @param tasks the list of tasks to analyze
     * @return a DashboardStats object containing task counts by priority
     */
    public static DashboardStats calculateStats(ArrayList<Task> tasks) {
        int urgent = 0;
        int high = 0;
        int medium = 0;
        int low = 0;

        for (Task task : tasks) {
            String priority = task.getPriority().getText();

            if ("Urgent!".equals(priority)) {
                urgent++;
            } else if ("High".equals(priority)) {
                high++;
            } else if ("Medium".equals(priority)) {
                medium++;
            } else if ("Low".equals(priority)) {
                low++;
            }
        }

        return new DashboardStats(tasks.size(), urgent, high, medium, low);
    }
}