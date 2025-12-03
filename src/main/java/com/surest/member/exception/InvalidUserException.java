package com.surest.member.exception;

public class InvalidUserException extends RuntimeException {
    
    public InvalidUserException(String message) {
        super(message);
    }
    
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
