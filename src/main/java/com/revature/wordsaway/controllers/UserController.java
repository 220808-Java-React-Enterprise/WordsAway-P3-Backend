package com.revature.wordsaway.controllers;
import com.revature.wordsaway.dtos.responses.FindUserResponse;
import com.revature.wordsaway.entities.User;
import com.revature.wordsaway.services.TokenService;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping
public class UserController {

    @CrossOrigin
    @GetMapping(value = "/findUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody FindUserResponse finduser(HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            resp.setStatus(200);
            return UserService.getFriendByUsername(user.getUsername());
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            //return e.getMessage();
            return null;
        }
    }


}
