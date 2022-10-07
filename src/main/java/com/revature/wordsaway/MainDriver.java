package com.revature.wordsaway;

import com.revature.wordsaway.utils.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainDriver {
    public static void main(String[] args) {
        SpringApplication.run(MainDriver.class, args);

        int port;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException nfe) {
            port = 5000;
        }
        ChatServer cs = new ChatServer(port);
        cs.start();
    }
}