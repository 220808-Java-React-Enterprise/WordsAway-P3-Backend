package com.revature.wordsaway.dtos.responses;

public class FindUserResponse {

    private String username;
    private float elo;
    private int gamesPlayed;
    private int gamesWon;

    public FindUserResponse(String username, float elo, int gamesPlayed, int gamesWon) {
        this.username = username;
        this.elo = elo;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
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
}
