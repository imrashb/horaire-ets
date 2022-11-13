package me.imrashb.exception;

import me.imrashb.parser.*;

import java.util.*;

public class TooManyCoursException extends RuntimeException {

    public TooManyCoursException() {
        super("Vous pouvez générer des horaires avec un maximum de "+ GenerateurHoraire.MAX_NB_COURS +" cours à la fois");
    }

}
