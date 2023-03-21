package me.imrashb.service;

import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.exception.InvalidEncodedIdException;

import java.util.*;

class CombinaisonHoraireFactory {

    private static List<Groupe> getGroupes(Map<String, String> mapGroupes, List<Cours> cours) {
        List<Groupe> groupes = new ArrayList<>();
        for (Cours c : cours) {
            if (mapGroupes.containsKey(c.getSigle())) {
                String num = mapGroupes.get(c.getSigle());
                String trimmed = num.substring(0, 2);
                Groupe groupe = null;
                for (Groupe g : c.getGroupes()) {
                    if (g.getNumeroGroupe().equals(trimmed)) {
                        if (trimmed.equals(num)) {
                            groupe = g;
                            break;
                        } else {
                            // SubGroupe
                            for (Groupe sub : g.createSubGroupes()) {
                                if (sub.getNumeroGroupe().equals(num)) {
                                    groupe = sub;
                                    break;
                                }
                            }

                            if (groupe != null) break;
                        }
                    }
                }
                if (groupe == null)
                    throw new InvalidEncodedIdException("Le groupe '" + c.getSigle() + Groupe.SEPARATEUR_SIGLE_NUM_GROUPE + num + "' n'existe pas.");

                for (Groupe g : groupes) {
                    if (g.overlapsWith(groupe))
                        throw new InvalidEncodedIdException("La combinaison d'horaire contient des chevauchements de cours.");
                }
                groupes.add(groupe);
            }
        }
        return groupes;
    }

    /**
     * Factory method pour créer la combinaison avec un unique id encodé
     *
     * @param encodedUniqueId l'id unique encodé en Base64
     * @return La combinaison d'horaire
     */
    public static CombinaisonHoraire fromEncodedUniqueId(String encodedUniqueId, SessionService sessionService) {
        if (encodedUniqueId.getBytes().length < 2) throw new InvalidEncodedIdException("L'identifiant est trop petit.");

        String decoded = null;
        try {
            decoded = new String(Base64.getDecoder().decode(encodedUniqueId.getBytes()));
        } catch (Exception ex) {
            throw new InvalidEncodedIdException("L'identifiant ne peut pas être décodé.");
        }

        String[] split = decoded.split(CombinaisonHoraire.SEPARATEUR_SESSION + "");

        if (split.length != 2) throw new InvalidEncodedIdException("La session ou les cours sont manquants.");

        String nomSession = split[0];
        String nomsCours = split[1];

        List<Cours> cours = sessionService.getListeCours(nomSession);

        if (cours == null) throw new InvalidEncodedIdException("La session est invalide.");

        String[] idGroupes = nomsCours.split(CombinaisonHoraire.SEPARATEUR_GROUPES + "");

        Map<String, String> mapGroupes = new HashMap<>();
        for (String s : idGroupes) {
            String[] splitIdNum = s.split(Groupe.SEPARATEUR_SIGLE_NUM_GROUPE + "");
            if (splitIdNum.length != 2) throw new InvalidEncodedIdException("Le groupe '" + s + "' est malformé.");

            mapGroupes.put(splitIdNum[0], splitIdNum[1]);
        }

        if (idGroupes.length != mapGroupes.size()) throw new InvalidEncodedIdException("Il y a des cours redondants.");

        List<Groupe> groupes = getGroupes(mapGroupes, cours);
        if (groupes.size() != mapGroupes.size())
            throw new InvalidEncodedIdException("Certains cours sont inexistants.");

        return new CombinaisonHoraire(groupes);

    }

}
