package com.revature.wordsaway.dtos.requests;

import java.util.UUID;

public class BoardRequest {
    private UUID boardID;
    private char[] layout;
    private boolean replacedTray;

    public BoardRequest() {
    }

    public UUID getBoardID() {
        return boardID;
    }

    public char[] getLayout() {
        return layout;
    }

    public boolean isReplacedTray() { return replacedTray; }

    public void setBoardID(UUID boardID) {
        this.boardID = boardID;
    }

    public void setLayout(char[] layout) {
        this.layout = layout;
    }

    public void setReplacedTray(boolean replacedTray) {
        this.replacedTray = replacedTray;
    }

    @Override
    public String toString() {
        return "MoveRequest{" +
                "boardID=" + boardID +
                ", move=" + layout +
                ", replacedTray=" + replacedTray +
                '}';
    }
}
