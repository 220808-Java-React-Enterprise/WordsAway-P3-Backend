package com.revature.wordsaway.dtos.requests;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(){}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
