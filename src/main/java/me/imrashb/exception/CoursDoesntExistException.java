package me.imrashb.exception;

import java.util.List;

public class CoursDoesntExistException extends RuntimeException {

    public CoursDoesntExistException(List<String> cours) {
        super("Les cours suivants n'existent pas/ne sont pas présents dans cette session: " + cours);
    }

}
