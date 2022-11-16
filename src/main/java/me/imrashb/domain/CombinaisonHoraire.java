package me.imrashb.domain;

import lombok.Data;
import me.imrashb.exception.InvalidEncodedIdException;
import org.jetbrains.annotations.NotNull;

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

        StringBuilder sb = new StringBuilder();

        if(groupes.size() != 0) {
            sb.append(groupes.get(0).getCours().getSession().toString()).append(SEPARATEUR_SESSION);
        }

        for(Groupe g : groupes) {
            sb.append(g.toString()).append(SEPARATEUR_GROUPES);
        }
        this.uniqueId = sb.toString();
        // Enleve le '/' à la fin
        if(this.uniqueId.length() != 0) this.uniqueId = this.uniqueId.substring(0, this.uniqueId.lastIndexOf(SEPARATEUR_GROUPES));

        for(Groupe g : groupes) {
            for(Activite a : g.getActivites()) {
                this.conges.remove(a.getHoraire().getJour());
            }
        }
    }

    public String getEncodedUniqueId() {
        return Base64.getEncoder().encodeToString(this.uniqueId.getBytes());
    }

}
