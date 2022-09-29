package com.revature.wordsaway.utils.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends NetworkException{
    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
