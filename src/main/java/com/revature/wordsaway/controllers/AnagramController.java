package com.revature.wordsaway.controllers;

import com.revature.wordsaway.services.AnagramService;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/anagram")
public class AnagramController {
    // TODO remove from final to prevent cheating or make only accessible by CUPs
    @CrossOrigin
    @GetMapping(value = "/best", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String anagramBest(@Param("letters") String letters, HttpServletResponse resp){
        try {
            return AnagramService.getBest(letters);
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    // TODO remove from final to prevent cheating or make only accessible by CUPs
    @CrossOrigin
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String anagramAll(@Param("letters") String letters, HttpServletResponse resp){
        try{
            return AnagramService.getAll(letters);
        } catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @CrossOrigin
    @GetMapping(value = "/isWord", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean anagramIsWord(@Param("letters") String letters, HttpServletResponse resp){
        try {
            return AnagramService.isWord(letters);
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return false;
        }
    }
}
