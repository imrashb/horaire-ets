package me.imrashb.exception;

public class InvalidEncodedIdException extends RuntimeException{

    public InvalidEncodedIdException(String raison) {
        super("Cet ID unique de combinaison de cours est invalide. Raison: "+raison);
    }

}
