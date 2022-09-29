package com.revature.wordsaway.controllers;

import com.revature.wordsaway.dtos.responses.FindUserResponse;
import com.revature.wordsaway.models.entities.Board;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.services.BoardService;
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
public class UserController {

    @CrossOrigin
    @GetMapping(value = "/findUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody FindUserResponse findUser(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            return UserService.getFriendByUsername(username);
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }
    }

    @CrossOrigin
    @PostMapping(value = "/addFriend", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean addFriend(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            UserService.addFriend(user, username);
            return true;
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return false;
        }
    }

    @CrossOrigin
    @PostMapping(value = "/removeFriend", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean removeFriend(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            UserService.removeFriend(user, username);
            return true;
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return false;
        }
    }

    @CrossOrigin
    @GetMapping(value = "/gameHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Board> getGameHistory (@RequestParam(required = false) String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            return BoardService.getAllByUsername(username != null ? username : user.getUsername());
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return new ArrayList<Board>();
        }
    }
}
