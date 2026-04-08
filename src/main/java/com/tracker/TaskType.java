package com.tracker;

/**
 * Represents a task type that can be assigned to tasks.
 * Defines the category for example appointment or issue.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/7/2026
 */

public class TaskType {
    private final int id;
    private final String name;

    /**
     * Constructs a task type with specified ID and name
     *
     * @param id identifier of the task type
     * @param name name of task type
     */
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