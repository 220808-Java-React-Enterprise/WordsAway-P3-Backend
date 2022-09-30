package com.revature.wordsaway.controllers;

import com.revature.wordsaway.dtos.responses.UserResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class UserController {

    @CrossOrigin
    @GetMapping(value = "/findUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody UserResponse findUser(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
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

    @GetMapping(value = "/getFriendsList", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, List<UserResponse>> getFriendsList(HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            return UserService.getFriendsList(user.getUsername());
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            Map<String, List<UserResponse>> emptyMap = new HashMap<>();
            emptyMap.put("friends",  new ArrayList<>());
            emptyMap.put("incomingRequests",  new ArrayList<>());
            emptyMap.put("outgoingRequests",  new ArrayList<>());
            return emptyMap;
        }

    }

    @PostMapping(value = "/addFriend", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean addFriend(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            UserService.addFriend(user.getUsername(), username);
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
            UserService.removeFriend(user.getUsername(), username);
            return true;
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return false;
        }
    }

    @PostMapping(value = "/acceptFriendRequest", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean acceptFriendRequest(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        return addFriend(username, req, resp);
    }

    @PostMapping(value = "/rejectFriendRequest", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean rejectFriendRequest(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            UserService.removeFriend(username, user.getUsername());
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
