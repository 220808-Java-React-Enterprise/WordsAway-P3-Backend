package com.revature.wordsaway.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Constants {
    public static final int BOARD_SIZE = 16;
    public static final int TOTAL_WORM_LENGTHS = 17; // (5, 4, 3, 3, 2)
    public static final Map<Integer, Integer> START_POINT_BY_WORD_LENGTH = new TreeMap<>();
    // Map<Word length, Starting point in VALID_WORDS>
    public static final List<String> VALID_WORDS = populateValidWords();

    private static List<String> populateValidWords(){
        List<String> words = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File("src/main/resources/validWordSet.txt"));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (!Constants.START_POINT_BY_WORD_LENGTH.containsKey(line.length()))
                    Constants.START_POINT_BY_WORD_LENGTH.put(line.length(), words.size());

                words.add(line);
            }
            START_POINT_BY_WORD_LENGTH.put(BOARD_SIZE, words.size());
            START_POINT_BY_WORD_LENGTH.put(BOARD_SIZE + 1, words.size());
            return words;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
