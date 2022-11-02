package me.imrashb.domain;

public enum Trimestre {

    HIVER(1),
    ETE(2),
    AUTOMNE(3);

    private int numeroSession;

    Trimestre(final int numeroSession) {
        this.numeroSession = numeroSession;
    }

    public String getIdTrimestre(final int annee) {
        return annee + "" + numeroSession;
    }

    public static Trimestre getTrimestreFromId(String id) {
        int parsed = Integer.parseInt(id.substring(id.length()-1));
        for(Trimestre t : Trimestre.values()) {
            if(t.numeroSession == parsed) return t;
        }
        return null;
    }
}
