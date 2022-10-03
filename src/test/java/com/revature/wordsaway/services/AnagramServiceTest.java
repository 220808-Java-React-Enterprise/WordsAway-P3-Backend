package com.revature.wordsaway.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnagramServiceTest {
    private static AnagramService anagramService;

    @BeforeEach
    public void setup(){
        anagramService = new AnagramService();
    }

    @Test
    public void testGetAll_succeed(){
        char[] tray = new char[] {'U','G','E','B','P','D','X'};
        String pattern = "_______";

        while (pattern.length() > 1){
            List<String> getAllList = anagramService.getAll(tray, pattern);
            //System.out.println(getAllList);
            pattern = pattern.substring(0, pattern.lastIndexOf("_"));
        }
    }

    @Test
    public void isWord_succeed(){
        String letters = "healing";

        boolean flag = anagramService.isWord(letters);

        assertTrue(flag);
    }

    @Test
    public void isWord_fail(){
        String letters = "henvkj";

        boolean flag = anagramService.isWord(letters);

        assertFalse(flag);
    }
}