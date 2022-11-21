package me.imrashb.exception;

import me.imrashb.parser.GenerateurHoraire;

public class InvalidCoursAmountException extends RuntimeException {

    public InvalidCoursAmountException(int amount) {
        super("Vous ne pouvez pas générer des horaires de " + amount + " cours. Vous pouvez seulement générer des horaires avec " + GenerateurHoraire.MIN_NB_COURS + " jusqu'à " + GenerateurHoraire.MAX_NB_COURS + ".");
    }

}
