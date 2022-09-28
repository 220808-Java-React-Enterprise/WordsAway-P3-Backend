package com.revature.wordsaway.dtos.responses;

public class AnagramResponse {
    private int found;

    public int getFound() {
        return found;
    }

    @Override
    public String toString() {
        return "AnagramResponse{" +
                "result=" + found +
                '}';
    }
}
