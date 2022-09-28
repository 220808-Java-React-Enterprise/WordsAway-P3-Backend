package com.revature.wordsaway.services;

import com.revature.wordsaway.models.Board;
import org.springframework.stereotype.Service;
import java.util.*;

import static com.revature.wordsaway.utils.Constants.BOARD_SIZE;

// todo run bot while user is making move to negate wait time on bot
@Service
public class AIService {
    private static List<WordAndLocation> finalList;
    private static List<String> validWords;
    private static String existingLetters;
    private static Board board;
    private static char[] letters;
    private static char[] tray;
    private static final Random rand = new Random(System.currentTimeMillis());

    public void setRandomSeed(int seed){
        rand.setSeed(seed);
    }

    private static class WordAndLocation{
        private int location;
        private String word;
    }

    public static Board start(long startTime, Board board){ // todo add level for bots
        // If bot has taken longer than 20 seconds leave
        if (System.currentTimeMillis() - startTime > 20000) return board;
        finalList = new ArrayList<>();
        AIService.board = board;
        letters = board.getLetters();
        tray = board.getTray();
        int increment;

        // todo add other bots here
        // If increment is -1 means a fireball was cast
        if ((increment = easyBot()) != -1) {
            // Check if list is empty
            if (finalList.isEmpty()) {
                Board newBoard = start(startTime, board.clone());

                if (Arrays.equals(newBoard.getLetters(), board.getLetters()) && board.getFireballs() > 0) {
                    shootFireBall();
                    return board;
                }
                return newBoard;
            }
            // Get random answer and play it
            WordAndLocation wl = finalList.get(rand.nextInt(finalList.size()));
            finalizeMove(wl, increment);
        }
        return board;
    }

    private static int easyBot(){
        // Get a random colum or row
        int start;
        boolean col = (start = rand.nextInt(BOARD_SIZE + BOARD_SIZE)) % 2 == 0;
        start /= 2;

        if (board.getFireballs() > 0 && rand.nextInt(100) % 20 == 0) {
            shootFireBall();
            return -1;
        }

        // Establish increment variable
        int increment;
        if (col) increment = BOARD_SIZE;
        else { increment = 1; start *= BOARD_SIZE; }

        int curr = start;
        int counter = 0;
        // Get a final list of all moves in give row or col
        while (counter < BOARD_SIZE - 2) {
            existingLetters = getExistingLetters(curr, increment);
            validWords = getWordList(existingLetters, curr, increment);
            finalList.addAll(getFinalWordListAndLocation(validWords, curr, increment));

            while (isLoop(col, start, curr) && isLetter(curr) && letters[curr] != '*'){
                curr += increment;
                counter++;
            }
            curr += increment;
            counter++;
        }
        return increment;
    }

    private static String getExistingLetters(int start, int increment){
        StringBuilder sb = new StringBuilder();
        StringBuilder spacer = new StringBuilder();

        // Get the beginning of the loop
        int spacerCounter = 0, counter = increment == BOARD_SIZE ? start / BOARD_SIZE : start % BOARD_SIZE;

        // Loop until end of colum or row
        for (int i = start; spacerCounter < tray.length && counter < BOARD_SIZE; i += increment){
            // Check if we are at a '.'
            if (isLetter(i)) {
                if (letters[i] != '*') {
                    sb.append(spacer.append(letters[i]));
                    spacer.delete(0, spacer.length());
                }
            }
            // If there is a value in sb then add a spacer to spacer
            else {
                spacer.append("_");
                spacerCounter++;
            }
            counter++;
        }
        return sb.toString();
    }

    private static List<String> getWordList(String pattern, int start, int increment) {
        List<String> words = new ArrayList<>();
        List<String> incomingWords;

        int rowOrCol = increment == BOARD_SIZE ? start / BOARD_SIZE : start % BOARD_SIZE,
            maxWordLength = tray.length - pattern.replaceAll("[A-Z]", "").length() + pattern.length(),
            wordLength = Math.min(maxWordLength, BOARD_SIZE - rowOrCol);

        // Loop for all possible words in that given space
        do {
            incomingWords = AnagramService.getAllList(String.valueOf(tray), pattern, wordLength);
            if (incomingWords != null)
                words.addAll(incomingWords);

            if (pattern.lastIndexOf("_") == -1) break;
            pattern = pattern.substring(0, pattern.lastIndexOf("_"));
            wordLength = pattern.length();
        } while (pattern.length() > 2);

        return words;
    }

    private static List<WordAndLocation> getFinalWordListAndLocation(List<String> words, int start, int increment){
        List<WordAndLocation> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        WordAndLocation wl;
        int index;
        char[] c;
        boolean col = increment == BOARD_SIZE;
        // Switch increment for validity
        int newIncrement = col ? 1 : BOARD_SIZE;

        // Loop for each word
        for (String word : words){
            // Index for each char in word
            index = 0;
            c = word.toCharArray();
            exit:{
                // Loop to validate word
                for (int j = start; index < word.length(); j += increment){
                    // Clear sb
                    sb.delete(0, sb.length());
                    // Check if letter fits in current location
                    if (isLetter(j) && letters[j] != c[index]) break exit;

                    // Letters in the neg direction
                    for (int cw = j - newIncrement; cw >= 0 && isLoop(!col, j, cw) && isLetter(cw) && letters[cw] != '*'; cw -= newIncrement)
                        sb.insert(0, sb.length() != 0 ? letters[cw] : String.valueOf(letters[cw]) + c[index]);

                    // Letters in the pos direction
                    for (int cw = j + newIncrement; cw < letters.length && isLoop(!col, j, cw) && isLetter(cw) && letters[cw] != '*'; cw += newIncrement)
                        sb.append(sb.length() != 0 ? letters[cw] : String.valueOf(c[index]) + letters[cw]);

                    // Validate word
                    if (sb.length() > 0 && !AnagramService.isWord(sb.toString().toLowerCase())) break exit;

                    index++;
                }
                wl = new WordAndLocation();
                wl.location = start;
                wl.word = word;
                list.add(wl);
            }
        }
        return list;
    }

    private static void finalizeMove(WordAndLocation wl, int increment){
        int counter = 0;
        // Word being played
        char[] c = wl.word.toCharArray();
        for (int i = wl.location; counter < c.length; i += increment) {
            letters[i] = c[counter];
            counter++;
        }
        board.setLetters(letters);
    }

    // todo adjust fireball aim
    private static void shootFireBall(){
        int target = rand.nextInt(BOARD_SIZE * BOARD_SIZE);
        while (isLetter(target) || letters[target] == '*')
            target = rand.nextInt(BOARD_SIZE * BOARD_SIZE);
        letters[target] = '*';
        board.setLetters(letters);
    }

    private static boolean isLoop(boolean col, int start, int curr){
        if (col)
            return curr < BOARD_SIZE * BOARD_SIZE && curr >= 0;
        return start / BOARD_SIZE == curr / BOARD_SIZE;
    }

    private static boolean isLetter(int idx){
        return String.valueOf(letters[idx]).matches("[A-Z]");
    }
}