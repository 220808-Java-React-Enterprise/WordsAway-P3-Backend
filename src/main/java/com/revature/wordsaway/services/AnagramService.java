package com.revature.wordsaway.services;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.*;
import com.revature.wordsaway.dtos.responses.AnagramResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnagramService {
    private static RestTemplate restTemplate = new RestTemplate();
    private static WebClient client = new WebClient();

    public AnagramService(RestTemplate restTemplate, WebClient client){
        AnagramService.restTemplate = restTemplate;
        AnagramService.client = client;
    }

    public static String getBest(String letters){
        try {
            return restTemplate.getForObject("http://www.anagramica.com/best/" + letters, String.class);
        }catch (HttpClientErrorException e){
            return "";
        }
    }

    public static String getAll(String letters){
        try {
            return restTemplate.getForObject("http://www.anagramica.com/all/" + letters, String.class);
        }catch (HttpClientErrorException e){
            return "";
        }
    }

    @ExceptionHandler(value = {IOException.class, FailingHttpStatusCodeException.class})
    public static boolean isWord(String letters) {
        StringBuilder pattern = new StringBuilder();
        setOptions();

        for (char c : letters.toCharArray())
            pattern.append("_");

        String pageURL = "https://anagram-solver.io/words-for/" + letters.toLowerCase() + "/pattern/" + pattern + "/?dictionary=otcwl";

        try {
            // Make request
            HtmlPage page = client.getPage(pageURL);

            // Get all anagrams
            List<HtmlElement> items = page.getByXPath("//div[@class='wordblock']/a");

            for (HtmlElement item : items){
                // Save to a list
                String word = item.asNormalizedText();

                if (word.matches(letters)) return true;
            }
        } catch (IOException | FailingHttpStatusCodeException e){
            return false;
        }
        return false;
    }

    @ExceptionHandler(value = {IOException.class, FailingHttpStatusCodeException.class})
    public static List<String> getAllList(String letters, String pattern, int wordLength){
        List<String> words = new ArrayList<>();
        setOptions();

        String pageURL = !pattern.equals("")
                ? "https://anagram-solver.io/words-for/" + letters + "/pattern/" + pattern + "/?dictionary=otcwl"
                : "https://anagram-solver.io/words-for/" + letters + "/pattern/___/?dictionary=otcwl";

        try {
            // Make request
            HtmlPage page = client.getPage(pageURL);

            // Get all anagrams
            List<HtmlElement> items = page.getByXPath("//div[@class='wordblock']/a");

            for (HtmlElement item : items){
                // Save to a list
                String word = item.asNormalizedText().toUpperCase();

                if (word.length() == 2) break;

                // Check if it's the pattern
                if (!word.equals(pattern.replace("_", "")))
                    // Skip if length of word is greater than what we want
                    if (word.length() <= wordLength)
                        words.add(word);
            }
        } catch (IOException | FailingHttpStatusCodeException e){
            return null;
        }
        return words;
    }

    private static void setOptions() {
        try{
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
        } catch (NullPointerException ignored){}
    }
}
