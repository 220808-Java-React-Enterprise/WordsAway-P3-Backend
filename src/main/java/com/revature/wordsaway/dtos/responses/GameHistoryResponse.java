package com.revature.wordsaway.dtos.responses;

import com.revature.wordsaway.models.enums.GameState;
import java.util.UUID;

public class GameHistoryResponse {
    private UUID id;
    private UserResponse opponent;
    private GameState outcome;

    public GameHistoryResponse(UUID id, UserResponse opponent, GameState outcome) {
        this.id = id;
        this.opponent = opponent;
        this.outcome = outcome;
    }

    public UUID getId() {
        return id;
    }

    public UserResponse getOpponent() {
        return opponent;
    }

    public GameState getOutcome() {
        return outcome;
    }

    @Override
    public String toString() {
        return "GameHistoryResponse{" +
                "id=" + id +
                ", opponent=" + opponent +
                ", outcome=" + outcome +
                '}';
    }
}
