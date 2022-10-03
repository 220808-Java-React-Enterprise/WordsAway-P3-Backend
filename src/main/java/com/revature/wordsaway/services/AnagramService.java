package com.revature.wordsaway.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.revature.wordsaway.utils.Constants.*;

@Service
public class AnagramService {
    public static List<String> getAll(char[] tray, String pattern){
        List<String> words = new ArrayList<>();
        Arrays.sort(tray);
        int trayIdx, start = START_POINT_BY_WORD_LENGTH.get(pattern.length());

        char[] currWord;
        for (int wordIdx = start; wordIdx < START_POINT_BY_WORD_LENGTH.get(pattern.length() + 1); wordIdx++) {
            currWord = VALID_WORDS.get(wordIdx).toCharArray();
            Arrays.sort(currWord);

            inner: {
                if (pattern.contains(VALID_WORDS.get(wordIdx))) break inner;

                trayIdx = 0;
                for (int letterIdx = 0; letterIdx < currWord.length;){
                    if (trayIdx == tray.length || tray[trayIdx] > currWord[letterIdx]) break inner;
                    else if (tray[trayIdx] == currWord[letterIdx]) letterIdx++;
                    trayIdx++;
                }
                words.add(VALID_WORDS.get(wordIdx));
            }
        }
        return words;
    }

    public static boolean isWord(String letters){
        for (int i = START_POINT_BY_WORD_LENGTH.get(letters.length()); i < START_POINT_BY_WORD_LENGTH.get(letters.length() + 1); i++)
            if (VALID_WORDS.get(i).equals(letters.toUpperCase())) return true;
        return false;
    }
}
