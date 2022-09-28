package com.revature.wordsaway.dtos.requests;

public class GameRequest {
    private String username;

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "GameRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
