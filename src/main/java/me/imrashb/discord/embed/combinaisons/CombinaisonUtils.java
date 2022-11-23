package me.imrashb.discord.embed.combinaisons;

import me.imrashb.domain.Activite;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.HoraireActivite;
import me.imrashb.domain.combinaison.CombinaisonHoraire;

public class CombinaisonUtils {

    public static final String[] SYMBOLES_COURS = {":blue_square:", ":orange_square:", ":purple_square:", ":red_square:", ":yellow_square:", ":white_large_square:"};
    private static final HoraireActivite HORAIRE_MATIN = new HoraireActivite(6, 0, 12, 30, null);
    private static final HoraireActivite HORAIRE_MIDI = new HoraireActivite(13, 0, 17, 30, null);
    private static final HoraireActivite HORAIRE_SOIR = new HoraireActivite(18, 0, 23, 0, null);
    private static final String SYMBOLE_EMPTY = ":black_large_square:";

    public static String getCombinaisonString(CombinaisonHoraire combinaison) {

        String[][] periodes = new String[7][3];

        int counter = 0;

        for (Groupe groupe : combinaison.getGroupes()) {
            String symbole = SYMBOLES_COURS[counter];
            for (Activite activite : groupe.getActivites()) {

                int id = activite.getHoraire().getJour().getId();
                HoraireActivite horaire = activite.getHoraire();
                if (HORAIRE_MATIN.overlapsWithIgnoreJour(horaire)) {
                    periodes[id][0] = symbole;
                }

                if (HORAIRE_MIDI.overlapsWithIgnoreJour(horaire)) {
                    periodes[id][1] = symbole;
                }

                if (HORAIRE_SOIR.overlapsWithIgnoreJour(horaire)) {
                    periodes[id][2] = symbole;
                }
            }
            counter++;
        }

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < periodes[0].length; j++) {
            for (String[] periode : periodes) {
                String symbole = periode[j];
                if (symbole == null) sb.append(SYMBOLE_EMPTY);
                else sb.append(symbole);
            }
            if (j == 0) {
                sb.append(" Matin");
            } else if (j == 1) {
                sb.append(" Après-midi");
            } else if (j == 2) {
                sb.append(" Soir");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
