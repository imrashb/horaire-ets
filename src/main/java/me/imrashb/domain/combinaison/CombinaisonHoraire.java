package me.imrashb.domain.combinaison;

import lombok.Data;
import me.imrashb.domain.Activite;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.Jour;

import java.util.*;

@Data
public class CombinaisonHoraire {

    public static final char SEPARATEUR_GROUPES = '/';
    public static final char SEPARATEUR_SESSION = ':';
    private static final List<Jour> LISTE_JOURS = new ArrayList<>(Arrays.asList(Jour.values()));
    private List<Groupe> groupes;
    private List<Jour> conges = new ArrayList<>(LISTE_JOURS);
    private String uniqueId = "";

    public CombinaisonHoraire(List<Groupe> groupes) {
        this.groupes = groupes;
        Collections.sort(this.groupes);
        generateUniqueId();
        configureConges();
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

}

