package com.revature.wordsaway.controllers;

import com.revature.wordsaway.dtos.requests.UpdateUserRequest;
import com.revature.wordsaway.dtos.responses.GameHistoryResponse;
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
import java.util.*;

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

    @CrossOrigin
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
    @PostMapping(value = "/cancelFriend", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean cancelFriend(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
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


    @CrossOrigin
    @PostMapping(value = "/removeFriend", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean removeFriend(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            UserService.removeFriend(user.getUsername(), username);
            UserService.removeFriend(username, user.getUsername());
            return true;
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return false;
        }
    }


    @CrossOrigin
    @PostMapping(value = "/acceptFriendRequest", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean acceptFriendRequest(@RequestParam String username, HttpServletRequest req, HttpServletResponse resp) {
        return addFriend(username, req, resp);
    }


    @CrossOrigin
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
    public @ResponseBody List<GameHistoryResponse> getGameHistory (@RequestParam(required = false) String username, @RequestParam(required = false) Integer limit, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            List<Board> boards = BoardService.getAllByUsername(username != null ? username : user.getUsername());
            boards.sort((o1, o2) -> {
                if (o1.getCompleted() == null && o2.getCompleted() == null) {
                    return 0;
                } else if (o1.getCompleted() == null) return -1;
                else if (o2.getCompleted() == null) return 1;
                return o1.getCompleted().compareTo(o2.getCompleted());
            });
            int end = limit == null ? boards.size() : (limit < boards.size() ? limit : boards.size());
            List<GameHistoryResponse> gameHistories = new ArrayList<>();
            for(int i = 0; i < boards.size() && gameHistories.size() <= end; i++){
                if(boards.get(i).getCompleted() != null) {
                    User opponent = BoardService.getOpposingBoard(boards.get(i)).getUser();
                    gameHistories.add(new GameHistoryResponse(
                            boards.get(i).getId(),
                            new UserResponse(
                                    opponent.getUsername(),
                                    opponent.getELO(),
                                    opponent.getGamesPlayed(),
                                    opponent.getGamesWon(),
                                    opponent.getAvatar()
                            ),
                            boards.get(i).getGameState()));
                }
            }
            return gameHistories;
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }


    @CrossOrigin
    @GetMapping(value = "/getRankElo", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer getRankByElo(@RequestParam(required = false) String username, HttpServletRequest req, HttpServletResponse resp){
        try {
            User user = TokenService.extractRequesterDetails(req);
            if(username == null) { username = user.getUsername(); }
            return UserService.getRankByElo(username, UserService.getRankingsByELO());
        }catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return 0;
        }

    }

    @CrossOrigin
    @PutMapping(value = "/settings/updateUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String settingsUpdateUser(@RequestBody UpdateUserRequest request, HttpServletRequest req, HttpServletResponse resp){
        try{
            User user = TokenService.extractRequesterDetails(req);
            UserService.settingsUpdateUser(user.getUsername(), request);
            return "Settings updated!";

        }
        catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            return e.getMessage();
        }

    }
}
