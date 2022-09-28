package com.revature.wordsaway.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Constants {
    public static final int BOARD_SIZE = 16;
    public static final int TOTAL_WORM_LENGTHS = 17; // (5, 4, 3, 3, 2)
    public static final Set<String> VALID_WORDS = populateValidWords();

    private static Set<String> populateValidWords(){
        Set<String> words = new HashSet<>();

        try {
            Scanner scanner = new Scanner(new File("src/main/resources/validWordSet.txt"));

            while (scanner.hasNextLine())
                words.add(scanner.nextLine());

            return words;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
