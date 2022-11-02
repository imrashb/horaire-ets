package me.imrashb.controller;

import me.imrashb.exception.CoursDoesntExistException;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.TrimestreDoesntExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CombinaisonControllerAdvice {

    @ExceptionHandler({CoursNotInitializedException.class, TrimestreDoesntExistException.class, CoursDoesntExistException.class})
    public ResponseEntity handleNotFoundException(Exception exception) {
        return new ResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

}
