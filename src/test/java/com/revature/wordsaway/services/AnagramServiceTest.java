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

    private AnagramService anagramService;
    private WebClient mockClient;
    private RestTemplate mockRestTemplate;

    @BeforeEach
    public void setup(){
        mockRestTemplate = mock(RestTemplate.class);
        mockClient = mock(WebClient.class);
        anagramService = new AnagramService(mockRestTemplate, mockClient);
    }

    @AfterEach
    public void setdown(){
        anagramService = null;
        mockRestTemplate = null;
    }
}