package com.revature.wordsaway.dtos.responses;

import java.util.UUID;

public class OpponentResponse {
    private String username;
    private float elo;
    private UUID board_id;

    public OpponentResponse(String username, float elo, UUID board_id) {
        this.username = username;
        this.elo = elo;
        this.board_id = board_id;
    }

    public String getUsername() {
        return username;
    }

    public float getElo() {
        return elo;
    }

    public UUID getBoard_id() {
        return board_id;
    }

    @Override
    public String toString() {
        return "OpponentResponse{" +
                "username='" + username + '\'' +
                ", elo=" + elo +
                ", board_id=" + board_id +
                '}';
    }
}
