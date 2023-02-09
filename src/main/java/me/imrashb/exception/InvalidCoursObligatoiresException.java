package me.imrashb.exception;

public class InvalidCoursObligatoiresException extends RuntimeException{
    public InvalidCoursObligatoiresException() {
        super("Les cours obligatoires que vous avez sélectionnés doivent obligatoirement être tous dans les cours sélectionnés.");
    }
}
