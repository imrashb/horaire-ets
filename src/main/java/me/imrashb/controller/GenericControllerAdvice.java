package me.imrashb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericControllerAdvice {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        return new ResponseEntity("Missing parameter '" + name + "'. Type: '" + ex.getParameterType() + "'", HttpStatus.BAD_REQUEST);
    }

}
