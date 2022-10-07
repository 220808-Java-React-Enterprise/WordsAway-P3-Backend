package com.revature.wordsaway.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Constants {
    public static final int BOARD_SIZE = 16;
    public static final int TOTAL_WORM_LENGTHS = 17; // (5, 4, 3, 3, 2)
    public static final Map<Integer, Integer> START_POINT_BY_WORD_LENGTH = new TreeMap<>();
    // Map<Word length, Starting point in VALID_WORDS>
    public static final List<String> VALID_WORDS = populateValidWords();

    private static List<String> populateValidWords(){
        List<String> words = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Constants.class.getClassLoader().getResourceAsStream("validWordSet.txt"))));

        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (!Constants.START_POINT_BY_WORD_LENGTH.containsKey(line.length()))
                    Constants.START_POINT_BY_WORD_LENGTH.put(line.length(), words.size());

                words.add(line);
            }
            START_POINT_BY_WORD_LENGTH.put(BOARD_SIZE, words.size());
            START_POINT_BY_WORD_LENGTH.put(BOARD_SIZE + 1, words.size());
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}