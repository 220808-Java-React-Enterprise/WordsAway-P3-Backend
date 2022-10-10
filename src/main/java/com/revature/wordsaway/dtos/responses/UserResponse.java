package com.revature.wordsaway.dtos.responses;

public class UserResponse {

    private String username;
    private float elo;
    private int gamesPlayed;
    private int gamesWon;
    private int avatar;

    public UserResponse(String username, float elo, int gamesPlayed, int gamesWon, int avatar) {
        this.username = username;
        this.elo = elo;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public float getElo() {
        return elo;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getAvatar(){
        return avatar;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "username='" + username + '\'' +
                ", elo=" + elo +
                ", gamesPlayed=" + gamesPlayed +
                ", gamesWon=" + gamesWon +
                ", avatar=" + avatar +
                '}';
    }
}
