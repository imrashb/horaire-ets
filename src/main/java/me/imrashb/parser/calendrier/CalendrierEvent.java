package me.imrashb.parser.calendrier;

import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@ToString
public class CalendrierEvent {

    private String id;
    private Type type;
    private LocalDate date;
    private String description;

    public CalendrierEvent(Type type, LocalDate date, String description) {
        this.type = type;
        this.date = date;
        this.description = description;
        this.id = Base64.getEncoder().encodeToString((description+date.toString()).getBytes());
    }


    public enum Type {
        INSCRIPTION_ANCIENS_ETUDIANTS,
        INSCRIPTION_NOUVEAUX_ETUDIANTS,
        DEBUT_SESSION,
        FIN_SESSION,
        PERMUTATION_HORAIRE,
        REVISION_NOTE,
        PAIEMENT_DROITS_SCOLARITE

    }

}
