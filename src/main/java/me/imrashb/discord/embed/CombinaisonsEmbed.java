package me.imrashb.discord.embed;

import me.imrashb.domain.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.internal.requests.Route;

import java.util.*;

public class CombinaisonsEmbed extends CustomSlashCommandEmbed {
    private List<CombinaisonHoraire> combinaisons;

    private int currentCombinaison = 0;

    public CombinaisonsEmbed(List<CombinaisonHoraire> combinaisons) {
        this.combinaisons = combinaisons;
    }

    @Override
    public EmbedBuilder update() {

        if(currentCombinaison >= combinaisons.size()) {
            currentCombinaison = 0;
        } else if(currentCombinaison < 0 ) {
            currentCombinaison = combinaisons.size()-1;
        }

        embedBuilder.clear();

        CombinaisonHoraire comb = this.combinaisons.get(currentCombinaison);

        for(int i = 0; i<comb.getGroupes().size(); i++) {
            Groupe groupe = comb.getGroupes().get(i);
            embedBuilder.addField(groupe.toString(), SYMBOLES_COURS[i], true);
        }

        embedBuilder.setTitle("Horaire "+(currentCombinaison+1));
        embedBuilder.appendDescription(combinaisons.size()+" combinaisons trouvés");
        String stringCombinaison = getCombinaisonString(comb);
        embedBuilder.addField("Horaire", stringCombinaison, false);

        StringBuilder conges = new StringBuilder();
        comb.getConges().forEach(conge -> conges.append(conge.getNom()+", "));

        embedBuilder.addField("Congés", conges.toString(), true);

        return embedBuilder;
    }

    @Override
    protected List<FunctionalItemComponent> buildComponents() {
        FunctionalItemComponent prochain = new FunctionalItemComponent(
                Button.primary("prochain", "Prochain")
                        .withEmoji(Emoji.fromUnicode("➡")),
                (event) -> {
                    CombinaisonsEmbed.this.currentCombinaison++;
                });
        FunctionalItemComponent precedent = new FunctionalItemComponent(
                Button.primary("precedent", "Précédent")
                        .withEmoji(Emoji.fromUnicode("⬅")),
                (event) -> {
                    CombinaisonsEmbed.this.currentCombinaison--;
                });
        FunctionalItemComponent epingle = new FunctionalItemComponent(
                Button.secondary("epingle", "Épingler")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCC")),
                (event) -> {
                    // TODO - Send Message
                });
        FunctionalItemComponent partage = new FunctionalItemComponent(
                Button.secondary("partage", "Partager")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCE")),
                (event) -> {
                    setStayAlive(getStayAlive());
                });
        FunctionalItemComponent[] comps = {precedent, prochain, epingle, partage};
        return Arrays.asList(comps);
    }


    private final HoraireActivite HORAIRE_MATIN = new HoraireActivite(6, 0, 12, 30, null);
    private final HoraireActivite HORAIRE_MIDI = new HoraireActivite(13, 0, 17, 30, null);
    private final HoraireActivite HORAIRE_SOIR = new HoraireActivite(18, 0, 23, 0, null);

    private static final String[] SYMBOLES_COURS = {":blue_square:", ":orange_square:", ":purple_square:", ":red_square:", ":yellow_square:", ":white_large_square:"};
    private static final String SYMBOLE_EMPTY = ":black_large_square:";

    private String getCombinaisonString(CombinaisonHoraire combinaison) {

        String[][] periodes = new String[7][3];

        int counter = 0;

        for(Groupe groupe : combinaison.getGroupes()) {
            String symbole = SYMBOLES_COURS[counter];
            for(Activite activite : groupe.getActivites()) {

                int id = activite.getHoraire().getJour().getId();
                HoraireActivite horaire = activite.getHoraire();
                if(HORAIRE_MATIN.overlapsWithIgnoreJour(horaire)) {
                    periodes[id][0] = symbole;
                }

                if(HORAIRE_MIDI.overlapsWithIgnoreJour(horaire)) {
                    periodes[id][1] = symbole;
                }

                if(HORAIRE_SOIR.overlapsWithIgnoreJour(horaire)) {
                    periodes[id][2] = symbole;
                }
            }
            counter++;
        }

        StringBuilder sb = new StringBuilder();

        for(int j = 0; j<periodes[0].length; j++) {
            for(int i = 0; i<periodes.length; i++) {
                String symbole = periodes[i][j];
                if(symbole == null) sb.append(SYMBOLE_EMPTY);
                else sb.append(symbole);
            }
            if(j == 0) {
                sb.append(" Matin");
            } else if(j == 1) {
                sb.append(" Après-midi");
            } else if(j == 2) {
                sb.append(" Soir");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
