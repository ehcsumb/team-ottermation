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
  URGENT("Urgent!");

  private final String text;

  TaskPriority(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

}
