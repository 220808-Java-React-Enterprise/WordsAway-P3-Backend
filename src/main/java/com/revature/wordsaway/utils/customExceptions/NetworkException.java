package com.revature.wordsaway.utils.customExceptions;

public abstract class NetworkException extends RuntimeException {
    public NetworkException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
}
