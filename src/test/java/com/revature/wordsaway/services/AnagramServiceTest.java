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

    @Test
    public void test_getBest_succeed(){
        when(mockRestTemplate.getForObject("http://www.anagramica.com/best/test", String.class)).thenReturn("test");
        String result = AnagramService.getBest("test");
        verify(mockRestTemplate, times(1)).getForObject("http://www.anagramica.com/best/test", String.class);
        assertEquals(result, "test");
    }

    @Test
    public void test_getBest_fail(){
        when(mockRestTemplate.getForObject("http://www.anagramica.com/best/test", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        String result = AnagramService.getBest("test");
        verify(mockRestTemplate, times(1)).getForObject("http://www.anagramica.com/best/test", String.class);
        assertEquals(result, "");
    }

    @Test
    public void test_getAll_succeed(){
        when(mockRestTemplate.getForObject("http://www.anagramica.com/all/test", String.class)).thenReturn("test");
        String result = AnagramService.getAll("test");
        verify(mockRestTemplate, times(1)).getForObject("http://www.anagramica.com/all/test", String.class);
        assertEquals(result, "test");
    }

    @Test
    public void test_getAll_fail(){
        when(mockRestTemplate.getForObject("http://www.anagramica.com/all/test", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        String result = AnagramService.getAll("test");
        verify(mockRestTemplate, times(1)).getForObject("http://www.anagramica.com/all/test", String.class);
        assertEquals(result, "");
    }

    @Test
    public void test_isWord_succeed() throws IOException {
        HtmlPage mockPage = mock(HtmlPage.class);
        HtmlElement mockElement = mock(HtmlElement.class);

        when(mockClient.getPage("https://anagram-solver.io/words-for/test/pattern/____/?dictionary=otcwl")).thenReturn(mockPage);
        when(mockPage.getByXPath("//div[@class='wordblock']/a")).thenReturn(Collections.singletonList(mockElement));
        when(mockElement.asNormalizedText()).thenReturn("test");
        Boolean result = AnagramService.isWord("test");
        assertEquals(result, true);
    }

    @Test
    public void test_isWord_fail() throws IOException {
        HtmlPage mockPage = mock(HtmlPage.class);
        HtmlElement mockElement = mock(HtmlElement.class);


        when(mockClient.getPage("https://anagram-solver.io/words-for/va/pattern/__/?dictionary=otcwl")).thenReturn(mockPage);
        when(mockPage.getByXPath("//div[@class='wordblock']/a")).thenReturn(Collections.singletonList(mockElement));
        when(mockElement.asNormalizedText()).thenReturn("");
        Boolean result = AnagramService.isWord("va");
        assertEquals(result, false);
    }

    @Test
    public void test_isWord_fail_pageNotFound() throws IOException {
        HtmlPage mockPage = mock(HtmlPage.class);
        HtmlElement mockElement = mock(HtmlElement.class);


        when(mockClient.getPage("https://anagram-solver.io/words-for/va/pattern/__/?dictionary=otcwl")).thenThrow(IOException.class);
        when(mockPage.getByXPath("//div[@class='wordblock']/a")).thenReturn(Collections.singletonList(mockElement));
        when(mockElement.asNormalizedText()).thenReturn("");
        Boolean result = AnagramService.isWord("va");
        assertEquals(result, false);
    }

    @Test
    public void test_getAllList_succeed() throws IOException {
        HtmlPage mockPage = mock(HtmlPage.class);
        HtmlElement mockElement = mock(HtmlElement.class);


        when(mockClient.getPage("https://anagram-solver.io/words-for/test/pattern/___/?dictionary=otcwl")).thenReturn(mockPage);
        when(mockPage.getByXPath("//div[@class='wordblock']/a")).thenReturn(Collections.singletonList(mockElement));
        when(mockElement.asNormalizedText()).thenReturn("test");
        List<String> result = AnagramService.getAllList("test", "", 4);
        assertEquals(result, Arrays.asList("TEST"));
    }

    @Test
    public void test_getAllList_succeed_whenNoWordsAreFound() throws IOException {
        HtmlPage mockPage = mock(HtmlPage.class);
        HtmlElement mockElement = mock(HtmlElement.class);


        when(mockClient.getPage(anyString())).thenReturn(mockPage);
        when(mockPage.getByXPath("//div[@class='wordblock']/a")).thenReturn(Collections.singletonList(mockElement));
        when(mockElement.asNormalizedText()).thenReturn("");
        List<String> result = AnagramService.getAllList("gu", "", 4);
        assertEquals(result, Arrays.asList());
    }

    @Test
    public void test_getAllList_succeed_whenPageNotFound() throws IOException {
        HtmlPage mockPage = mock(HtmlPage.class);
        HtmlElement mockElement = mock(HtmlElement.class);


        when(mockClient.getPage(anyString())).thenThrow(IOException.class);
        when(mockPage.getByXPath("//div[@class='wordblock']/a")).thenReturn(Collections.singletonList(mockElement));
        when(mockElement.asNormalizedText()).thenReturn("");
        List<String> result = AnagramService.getAllList("gu", "", 4);
        assertEquals(result, null);
    }
}