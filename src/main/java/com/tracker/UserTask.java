package com.tracker;

/**
 * Adds user context to a generic Task
 * This extension is not strictly necessary, I just wanted to extend a class.
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class UserTask extends Task {

  private User user;
  private Integer id;

  public UserTask(User user, Task task, Integer id) {
    super();
    this.user = user;
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
