package com.revature.wordsaway;

import com.revature.wordsaway.entities.Board;
import com.revature.wordsaway.entities.User;
import com.revature.wordsaway.services.AIService;
import com.revature.wordsaway.services.AnagramService;
import com.revature.wordsaway.services.BoardService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

import static com.revature.wordsaway.utils.Constants.*;

@SpringBootApplication
public class MainDriver {
    public static void main(String[] args) {
        //SpringApplication.run(MainDriver.class, args);

        char[] tray = new char[7];
        BoardService.getNewTray(tray);
        char[] letters = new char[BOARD_SIZE*BOARD_SIZE];
        char[] worms = new char[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(letters, '.');
        Arrays.fill(worms, '.');

        User user = new User("username", "password", "salt", "email", 0, 500, 0, 0, true, new HashSet<>());
        Board board = new Board(UUID.randomUUID(), user, tray, 0, worms, letters, UUID.randomUUID(), true);

        System.out.println(START_POINT_BY_WORD_LENGTH);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 50; i++){
            board = AIService.start(System.currentTimeMillis(), board);
            BoardService.getNewTray(tray);
            board.setTray(tray);
        }

        System.out.println((float)(System.currentTimeMillis() - start) / 1000);
        System.out.println(AIService.temp);

        char[] newLetters = board.getLetters();
        int counter = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++)
                System.out.print(newLetters[counter++] + ", ");
            System.out.println();
        }
    }
}