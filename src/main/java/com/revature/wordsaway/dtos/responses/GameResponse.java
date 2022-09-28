package com.revature.wordsaway.dtos.responses;

import java.util.Arrays;

public class GameResponse {
    private char[] letters;
    private char[] worms;
    private char[] tray;
    private int fireballs;
    private boolean isActive;
    private String opponent;
    private String winner;

    public GameResponse(char[] letters, char[] worms, char[] tray, int fireballs, boolean isActive, String opponent, String winner) {
        this.letters = letters;
        this.worms = worms;
        this.tray = tray;
        this.fireballs = fireballs;
        this.isActive = isActive;
        this.opponent = opponent;
        this.winner = winner;
    }

    public char[] getLetters() {
        return letters;
    }

    public char[] getWorms() {
        return worms;
    }

    public char[] getTray() {
        return tray;
    }

    public int getFireballs() {
        return fireballs;
    }

    public String getOpponent() {
        return opponent;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getWinner(){
        return winner;
    }

    @Override
    public String toString() {
        return "GameResponse{" +
                "letters=" + Arrays.toString(letters) +
                ", worms=" + Arrays.toString(worms) +
                ", tray=" + Arrays.toString(tray) +
                ", fireballs=" + fireballs +
                ", isActive=" + isActive +
                ", opponent='" + opponent +
                ", winner='" + winner + '\'' +
                '}';
    }
}
