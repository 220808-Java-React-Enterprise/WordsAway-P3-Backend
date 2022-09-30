package com.revature.wordsaway.controllers;
import com.revature.wordsaway.dtos.responses.FindUserResponse;
import com.revature.wordsaway.dtos.responses.OpponentResponse;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.services.TokenService;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class LobbyController {
    @CrossOrigin
    @GetMapping(value = "/getOpponents", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<OpponentResponse> getOpponents( @RequestParam(required = false) String type, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            if(type == null) return UserService.getAllOpponents(user.getUsername());
            else if(type.equalsIgnoreCase("bot")) return UserService.getAllOpponents(user.getUsername(), true);
            else if (type.equalsIgnoreCase("human")) return UserService.getAllOpponents(user.getUsername(), false);
            else return new ArrayList<>();
        }catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getTopTenElo", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<FindUserResponse> getTopTenByElo(HttpServletRequest req, HttpServletResponse resp){
        try {
            User user = TokenService.extractRequesterDetails(req);
            return UserService.getTopTenByElo();
        }catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }

    }

    @CrossOrigin
    @GetMapping(value = "/getRankingsElo", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<FindUserResponse> getRankingsByElo(HttpServletRequest req, HttpServletResponse resp){
        try {
            User user = TokenService.extractRequesterDetails(req);
            return UserService.getRankingsByELO();
        }catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }

    }
}
