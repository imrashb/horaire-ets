package me.imrashb.exception;

import java.util.List;

public class CoursAlreadyPresentException extends RuntimeException {

    public CoursAlreadyPresentException() {
        super("Vous tentez de générer des horaires avec plusieurs fois le même cours. Veuillez mettre des cours différents uniquement.");
    }

}
