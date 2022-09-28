package com.revature.wordsaway.controllers;

import com.revature.wordsaway.dtos.requests.LoginRequest;
import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.models.User;
import com.revature.wordsaway.services.TokenService;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import com.revature.wordsaway.utils.customExceptions.ResourceConflictException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping
public class AccessController {

    @CrossOrigin
    @PostMapping(value = "/signup", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String signup(@RequestBody NewUserRequest request, HttpServletResponse resp) {
        try {
            resp.setStatus(201);
            return UserService.register(request).toString();
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @CrossOrigin
    @PostMapping(value = "/login", consumes = "application/json")
    public String login(@RequestBody LoginRequest request, HttpServletResponse resp) {
        try {
            String token = UserService.login(request);
            resp.setHeader("Authorization", token);
            resp.setHeader("Access-Control-Expose-Headers", "Authorization");
            return "Logged In";
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @CrossOrigin
    @GetMapping(value = "/salt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String salt(@Param("username") String username, HttpServletResponse resp) {
        User user;
        try{
            user = UserService.getByUsername(username);
            resp.setStatus(200);
            return user.getSalt();
        }catch (NetworkException e){
            System.out.println(e.getMessage());
            resp.setStatus(201);
            return UUID.randomUUID().toString().replace("-","");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/auth")
    public String auth(HttpServletRequest httpServletRequest, HttpServletResponse resp) {
        try {
            return TokenService.extractRequesterDetails(httpServletRequest).toString();
        } catch (NetworkException e) {
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}