package com.tracker;

/**
 * Brief description of what this class does
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/7/2026
 */

public class TaskType {
    private final int id;
    private final String name;

    public TaskType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}