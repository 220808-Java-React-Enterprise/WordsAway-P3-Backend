package com.revature.wordsaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainDriver {
    public static void main(String[] args) {
        SpringApplication.run(MainDriver.class, args);
/*
        int port;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException nfe) {
            port = 9000;
        }
        ChatServer cs = new ChatServer(port);
        cs.start();
    }
 */
    }
}