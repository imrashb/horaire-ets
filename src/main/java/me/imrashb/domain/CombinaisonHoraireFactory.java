package me.imrashb.domain;

import lombok.Data;
import me.imrashb.exception.InvalidEncodedIdException;
import me.imrashb.parser.GenerateurHoraire;
import me.imrashb.parser.NodeGroupe;

import java.util.*;

public class CombinaisonHoraireFactory {

    private static List<Groupe> getGroupes(Map<String, String> mapGroupes, List<Cours> cours) {
        List<Groupe> groupes = new ArrayList<>();
        for(Cours c : cours) {
            if(mapGroupes.containsKey(c.getSigle())) {
                String num = mapGroupes.get(c.getSigle());
                Groupe groupe = null;
                for(Groupe g : c.getGroupes()) {
                    if(g.getNumeroGroupe().equals(num)) {
                        groupe = g;
                        break;
                    }
                }
                if(groupe == null) throw new InvalidEncodedIdException("Le groupe '"+c.getSigle()+Groupe.SEPARATEUR_SIGLE_NUM_GROUPE+num+"' n'existe pas.");

                for(Groupe g : groupes) {
                    if(g.overlapsWith(groupe)) throw new InvalidEncodedIdException("La combinaison d'horaire contient des chevauchements de cours.");
                }
                groupes.add(groupe);
            }
        }
        return groupes;
    }

    /**
     * Factory method pour créer la combinaison avec un unique id encodé
     * @param encodedUniqueId l'id unique encodé en Base64
     * @return La combinaison d'horaire
     */
    public static CombinaisonHoraire fromEncodedUniqueId(String encodedUniqueId, CoursManager manager) {
        String decoded = new String(Base64.getDecoder().decode(encodedUniqueId.getBytes()));

        String[] split = decoded.split(CombinaisonHoraire.SEPARATEUR_SESSION+"");

        if(split.length != 2) throw new InvalidEncodedIdException("La session ou les cours sont manquants.");

        String nomSession = split[0];
        String nomsCours = split[1];

        List<Cours> cours = manager.getListeCours(nomSession);

        if(cours == null) throw new InvalidEncodedIdException("La session est invalide.");

        String[] idGroupes = nomsCours.split(CombinaisonHoraire.SEPARATEUR_GROUPES+"");

        Map<String, String> mapGroupes = new HashMap<>();
        for(String s : idGroupes) {
            String[] splitIdNum = s.split(Groupe.SEPARATEUR_SIGLE_NUM_GROUPE+"");
            if(splitIdNum.length != 2) throw new InvalidEncodedIdException("Le groupe '"+s+"' est malformé.");

            mapGroupes.put(splitIdNum[0], splitIdNum[1]);
        }

        if(idGroupes.length != mapGroupes.size()) throw new InvalidEncodedIdException("Il y a des cours redondants.");

        List<Groupe> groupes = getGroupes(mapGroupes, cours);
        if(groupes.size() != mapGroupes.size()) throw new InvalidEncodedIdException("Certains cours sont inexistants.");

        return new CombinaisonHoraire(groupes);

    }

}
