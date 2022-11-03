package me.imrashb.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class CombinaisonHoraire {

    private static final List<Jour> LISTE_JOURS = new ArrayList<>(Arrays.asList(Jour.values()));

    private List<Groupe> groupes;
    private List<Jour> conges = new ArrayList<>(LISTE_JOURS);

    public CombinaisonHoraire(List<Groupe> groupes) {
        this.groupes = groupes;

        for(Groupe g : groupes) {
            for(Activite a : g.getActivites()) {
                this.conges.remove(a.getHoraire().getJour());
            }
        }
    }

}
