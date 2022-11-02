package me.imrashb.exception;

import java.util.List;

public class TrimestreDoesntExistException extends RuntimeException {

    public TrimestreDoesntExistException(String trimestre) {
        super("Le trimestre '"+trimestre+"' n'existe pas.");
    }

}
