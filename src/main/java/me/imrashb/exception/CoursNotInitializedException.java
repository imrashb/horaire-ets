package me.imrashb.exception;

public class CoursNotInitializedException extends RuntimeException {

    public CoursNotInitializedException() {
        super("Les cours n'ont pas encore terminé de s'initialiser pour le moment. Réessayer dans quelques secondes.");
    }

}
