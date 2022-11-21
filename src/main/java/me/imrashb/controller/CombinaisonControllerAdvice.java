package me.imrashb.controller;

import me.imrashb.exception.CoursDoesntExistException;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.InvalidCoursAmountException;
import me.imrashb.exception.SessionDoesntExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CombinaisonControllerAdvice {

    @ExceptionHandler({SessionDoesntExistException.class, CoursDoesntExistException.class})
    public ResponseEntity handleNotFoundException(Exception exception) {
        return new ResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCoursAmountException.class)
    public ResponseEntity handleBadRequestException(Exception exception) {
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CoursNotInitializedException.class)
    public ResponseEntity handleUnavailableException(Exception exception) {
        return new ResponseEntity(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

}
