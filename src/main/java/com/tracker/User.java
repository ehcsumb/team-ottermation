package com.tracker;

/**
 * model class for a logged in user.
 * stores the user's info after a successful login so any scene can access it.
 *
 * @author Khiem Vo
 * @since 4/6/2026
 */
public class User {

    private int id;
    private String username;
    private String role;

    /**
     * creates a user with the given id, username, and role.
     *
     * @param id the user's database id
     * @param username the user's username
     * @param role either "user" or "admin"
     */
    public User(int id, String username, String role) {
        this.id       = id;
        this.username = username;
        this.role     = role;
    }

    /** @return the user's database id */
    public int getId()          { return id; }

    /** @return the user's username */
    public String getUsername() { return username; }

    /** @return the user's role */
    public String getRole()     { return role; }

    /**
     * checks if this user has the admin role.
     *
     * @return true if role is "admin", false otherwise
     */
    public boolean isAdmin() {
        return role.equals("admin");
    }
}
