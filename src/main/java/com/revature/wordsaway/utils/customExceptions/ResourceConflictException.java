package com.revature.wordsaway.utils.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceConflictException extends NetworkException{
    public ResourceConflictException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 409;
    }
}
