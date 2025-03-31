package com.FinalProject.UMS;

public class GlobalState {
    private static GlobalState instance;
    private User loggedInUser;

    private GlobalState() {
        // Private constructor to prevent instantiation
    }

    public static synchronized GlobalState getInstance() {
        if (instance == null) {
            instance = new GlobalState();
        }
        return instance;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
