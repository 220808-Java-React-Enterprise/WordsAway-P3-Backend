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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "/getFriendsList", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<FindUserResponse> getFriendsList(HttpServletRequest req, HttpServletResponse resp) {

        try {
            User user = TokenService.extractRequesterDetails(req);
            return UserService.getFriendsList(user.getUsername());
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }

    }

    // get all current friends
    // get all friends that you wait for them to accept (outgoing)
    // get all friends that waiting for you to accept (incoming)
    @GetMapping(value = "/getFullFriendsList", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, List<FindUserResponse>> getFullFriendsList(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, List<FindUserResponse>> allStatusFriends = new HashMap<>();
        String currentFriends = "currentFriends";
        String outGoingFriends = "outGoingFriends";
        String incomingFriends = "incomingFriends";
        try {
            User user = TokenService.extractRequesterDetails(req);

            // get 3 different types(all, incoming, out) of friends and store each type to each List<>()
            List<FindUserResponse> friends = UserService.getFriendsList(user.getUsername());
            List<FindUserResponse> outFriendsList = new ArrayList<>();
            List<FindUserResponse> inFriendList = new ArrayList<>();

            // stored different types of friends into a map and return the map to front end
            allStatusFriends.put(currentFriends, friends);
            allStatusFriends.put(outGoingFriends, outFriendsList);
            allStatusFriends.put(incomingFriends, inFriendList);

            return allStatusFriends;

        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }

    }


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
