package com.example.demo.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GroupException extends RuntimeException {
    public GroupException(String errorMessage) {
        super(errorMessage);
    }
}
