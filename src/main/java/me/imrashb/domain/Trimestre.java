package me.imrashb.domain;

import lombok.Getter;

public enum Trimestre {

    HIVER(1, 'H'),
    ETE(2, 'E'),
    AUTOMNE(3, 'A');

    @Getter
    private int numeroSession;

    @Getter
    private char lettre;

    Trimestre(final int numeroSession, final char nom) {
        this.numeroSession = numeroSession;
        this.lettre = nom;
    }

    public static Trimestre getTrimestreFromId(String id) {
        int parsed = Integer.parseInt(id.substring(id.length() - 1));
        for (Trimestre t : Trimestre.values()) {
            if (t.numeroSession == parsed) return t;
        }
        return null;
    }

    public Session getSession(final int annee) {
        return new Session(annee, this);
    }

}
