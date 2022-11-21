package me.imrashb.domain;

import lombok.Data;

import java.util.*;

@Data
public class CombinaisonHoraire {

    public static final char SEPARATEUR_GROUPES = '/';
    public static final char SEPARATEUR_SESSION = ':';
    private static final List<Jour> LISTE_JOURS = new ArrayList<>(Arrays.asList(Jour.values()));
    private List<Groupe> groupes;
    private List<Jour> conges = new ArrayList<>(LISTE_JOURS);
    private String uniqueId = "";

    private Integer tempsPerdu = null;

    public CombinaisonHoraire(List<Groupe> groupes) {
        this.groupes = groupes;
        Collections.sort(this.groupes);
        generateUniqueId();
        configureConges();
    }

    public int getTempsPerdu() {
        if (tempsPerdu == null) calculateTempsPerdu();
        return this.tempsPerdu;
    }

    private void calculateTempsPerdu() {

        Map<Jour, TempsPerduParJour> tempsParJour = new HashMap<>();

        for (Groupe g : groupes) {
            for (Activite activite : g.getActivites()) {
                TempsPerduParJour j = tempsParJour.get(activite.getHoraire().getJour());
                if (j == null) {
                    j = new TempsPerduParJour();
                    tempsParJour.put(activite.getHoraire().getJour(), j);
                }
                j.setMin(activite.getHoraire().getHeureDepart());
                j.setMax(activite.getHoraire().getHeureFin());
                j.incrementTotal(activite.getHoraire().getHeureFin() - activite.getHoraire().getHeureDepart());
            }
        }

        int total = 0;
        for (TempsPerduParJour j : tempsParJour.values()) {
            total += j.getTempsPerdu();
        }
        this.tempsPerdu = total;
    }

    private void configureConges() {
        for (Groupe g : groupes) {
            for (Activite a : g.getActivites()) {
                this.conges.remove(a.getHoraire().getJour());
            }
        }
    }

    private void generateUniqueId() {

        StringBuilder sb = new StringBuilder();

        if (groupes.size() != 0) {
            sb.append(groupes.get(0).getCours().getSession().toString()).append(SEPARATEUR_SESSION);
        }

        for (Groupe g : groupes) {
            sb.append(g.toString()).append(SEPARATEUR_GROUPES);
        }
        String tmp = sb.toString();
        // Enleve le '/' à la fin
        if (tmp.length() != 0) tmp = tmp.substring(0, tmp.lastIndexOf(SEPARATEUR_GROUPES));
        this.uniqueId = Base64.getEncoder().encodeToString(tmp.getBytes());
    }

    private class TempsPerduParJour {

        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;
        private int total = 0;

        public void setMin(int min) {
            this.min = Math.min(this.min, min);
        }

        public void setMax(int max) {
            this.max = Math.max(this.max, max);
        }

        public void incrementTotal(int increment) {
            this.total += increment;
        }

        public int getTempsPerdu() {
            return (max - min) - total;
        }

    }

}

