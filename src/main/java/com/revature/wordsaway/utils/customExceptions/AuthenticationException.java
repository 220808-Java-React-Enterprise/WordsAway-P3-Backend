package com.revature.wordsaway.utils.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends NetworkException{
    public AuthenticationException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
