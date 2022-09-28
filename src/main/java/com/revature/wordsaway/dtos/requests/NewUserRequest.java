package com.revature.wordsaway.dtos.requests;

public class NewUserRequest {
    private String username;
    private String email;
    private String password;
    private String salt;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public String toString() {
        return "NewUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}