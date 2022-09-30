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
        SpringApplication.run(MainDriver.class, args);
    }
}