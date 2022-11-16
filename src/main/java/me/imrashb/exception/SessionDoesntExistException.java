package me.imrashb.exception;

public class SessionDoesntExistException extends RuntimeException {

    public SessionDoesntExistException(String session) {
        super("La session '"+session+"' n'existe pas.");
    }

}
