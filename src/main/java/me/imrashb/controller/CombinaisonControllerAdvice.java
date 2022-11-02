package me.imrashb.controller;

import me.imrashb.exception.CoursDoesntExistException;
import me.imrashb.exception.CoursNotInitializedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CombinaisonControllerAdvice {

    @ExceptionHandler(CoursNotInitializedException.class)
    public ResponseEntity handleCoursNotInitializedException(CoursNotInitializedException exception) {
        return new ResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CoursDoesntExistException.class)
    public ResponseEntity handleCoursDoesntExistException(CoursDoesntExistException exception) {
        return new ResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

}
