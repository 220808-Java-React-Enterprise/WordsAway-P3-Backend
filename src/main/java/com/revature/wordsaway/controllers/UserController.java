package com.revature.wordsaway.controllers;
import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.dtos.responses.FindUserResponse;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping
public class UserController {

    @CrossOrigin
    @GetMapping(value = "/findUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody FindUserResponse finduser(@RequestParam(required = false) String username, HttpServletResponse req, HttpServletResponse resp) {
        try {
            resp.setStatus(200);
            return UserService.getByUsername(username);
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());

            System.out.println(e.getMessage());
            //return e.getMessage();
        }
    }
}
