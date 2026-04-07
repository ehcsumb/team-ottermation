package com.tracker;

// model class that represents a logged in user
// stores the user's info after a successful login
public class User {

    private int id;
    private String username;
    private String role;

    // constructor called after a successful login
    public User(int id, String username, String role) {
        this.id       = id;
        this.username = username;
        this.role     = role;
    }

    // getters so other classes can read the user's info
    public int getId()          { return id; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }

    // returns true if the user is an admin
    public boolean isAdmin() {
        return role.equals("admin");
    }
}