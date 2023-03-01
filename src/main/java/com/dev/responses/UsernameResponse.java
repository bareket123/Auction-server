package com.dev.responses;

public class UsernameResponse extends BasicResponse {
    private String username;

    public UsernameResponse(String username) {
        this.username = username;
    }

    public UsernameResponse(boolean success, Integer errorCode, String username) {
        super(success, errorCode);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
