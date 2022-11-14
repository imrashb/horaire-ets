package me.imrashb.domain;

public enum Trimestre {

    HIVER(1, 'H'),
    ETE(2, 'E'),
    AUTOMNE(3, 'A');

    private int numeroSession;
    private char nom;

    Trimestre(final int numeroSession, final char nom) {
        this.numeroSession = numeroSession;
        this.nom = nom;
    }

    public String getIdTrimestre(final int annee) {
        return annee + "" + numeroSession;
    }

    public String getNomTrimestre(final int annee) {return nom+""+annee; }


    public static Trimestre getTrimestreFromId(String id) {
        int parsed = Integer.parseInt(id.substring(id.length()-1));
        for(Trimestre t : Trimestre.values()) {
            if(t.numeroSession == parsed) return t;
        }
        return null;
    }
}
