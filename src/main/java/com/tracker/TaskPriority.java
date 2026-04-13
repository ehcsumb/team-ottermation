package com.tracker;

/**
 * Possible values for task priority
 *
 * @author Eric Holm
 * @since 4/6/26
 */
public enum TaskPriority {
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High"),
  URGENT("Urgent!"),
  UNKNOWN("Unknown");

  private final String text;

  TaskPriority(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public static TaskPriority fromText(String text) {
    for (TaskPriority priority : TaskPriority.values()) {
      if (priority.text.equalsIgnoreCase(text)) {
        return priority;
      }
    }
    // no match, return unknown
    return UNKNOWN;
  }

}
