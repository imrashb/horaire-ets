package me.imrashb.discord.embed;

import me.imrashb.domain.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.*;

import java.util.*;

public class CombinaisonsEmbed extends CustomSlashCommandEmbed {
    private List<CombinaisonHoraire> combinaisons;
    private List<Message> messages = new ArrayList<>();
    private int currentCombinaison = 0;

    public CombinaisonsEmbed(List<CombinaisonHoraire> combinaisons) {
        this.combinaisons = combinaisons;
        this.addEmbedListener(new EmbedListener() {
            @Override
            public void onEmbedDelete() {
                for(Message m : messages) {
                    m.delete().queue();
                }
            }
        });
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
    protected EmbedLayout buildLayout() {
        StatefulActionComponent<Button> prochain = new StatefulActionComponent<Button>(
                Button.primary("prochain", "Prochain")
                        .withEmoji(Emoji.fromUnicode("➡"))) {
            @Override
            public Button execute(GenericComponentInteractionCreateEvent event, Button button) {
                CombinaisonsEmbed.this.currentCombinaison++;
                return button;
            }
        };

        StatefulActionComponent<Button> precedent = new StatefulActionComponent<Button>(
                Button.primary("precedent", "Précédent")
                        .withEmoji(Emoji.fromUnicode("⬅"))) {
            @Override
            public Button execute(GenericComponentInteractionCreateEvent event, Button button) {
                CombinaisonsEmbed.this.currentCombinaison--;
                return button;
            }
        };

        StatefulActionComponent<StringSelectMenu> choix = new StatefulActionComponent<StringSelectMenu>(
                getCombinaisonSelectMenu()) {
            @Override
            public StringSelectMenu execute(GenericComponentInteractionCreateEvent event, StringSelectMenu menu) {
                StringSelectInteractionEvent e = (StringSelectInteractionEvent) event;
                String value = e.getValues().get(0);
                int start = Math.max(0, currentCombinaison-12);
                int end = Math.min(currentCombinaison+12, combinaisons.size()-1);
                if(value.equals(ID_HORAIRES_PRECEDENT)) {
                    currentCombinaison = Math.max(0, end-24);
                } else if(value.equals(ID_HORAIRES_SUIVANT)) {
                    currentCombinaison = Math.min(start+24, combinaisons.size()-1);
                } else {
                    int index = Integer.parseInt(value);
                    currentCombinaison = index;
                }
                return getCombinaisonSelectMenu();
            }
        };


        StatefulActionComponent<Button> epingle = new StatefulActionComponent<Button>(
                Button.secondary("epingle", "Épingler")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCC"))) {
            @Override
            public Button execute(GenericComponentInteractionCreateEvent event, Button button) {
                setStayAlive(!getStayAlive());
                return button.withLabel(getStayAlive() ? "Oublier" : "Épingler");
            }
        };
        StatefulActionComponent partage = new StatefulActionComponent<Button>(
                Button.secondary("partage", "Partager")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCE"))) {
            @Override
            public Button execute(GenericComponentInteractionCreateEvent event, Button button) {

                for(Message m : messages) {
                    m.delete().queue();
                }
                messages.clear();

                for(String s : getFullCombinaisonString(combinaisons.get(currentCombinaison))) {
                    event.getMessageChannel().sendMessage(s).queue(message -> {
                        messages.add(message);
                    });
                }

                return button;
            }
        };
        return new EmbedLayout().addActionRow(precedent, prochain).addActionRow(choix).addActionRow(epingle, partage);
    }

    private static final String ID_HORAIRES_PRECEDENT = "horaires_precedents";
    private static final String ID_HORAIRES_SUIVANT = "horaires_suivants";

    public StringSelectMenu getCombinaisonSelectMenu() {
        StringSelectMenu.Builder menu = StringSelectMenu.create("choix");

        List<SelectOption> options = new ArrayList<>();

        int lowerBound = Math.min(combinaisons.size()-currentCombinaison, 12);

        int start = this.currentCombinaison-(25-lowerBound);
        if(start<0) start =0;
        if(start>0) {
            SelectOption opt = SelectOption.of("Voir les horaires précédents", ID_HORAIRES_PRECEDENT);
            options.add(opt);
            start++;
        }

        for(int i = start; i<combinaisons.size(); i++) {
            if(options.size() == 24 && i != combinaisons.size()-1) {
                SelectOption opt = SelectOption.of("Voir les horaires suivants", ID_HORAIRES_SUIVANT);
                options.add(opt);
                break;
            }


            CombinaisonHoraire c = combinaisons.get(i);
            StringBuilder sb = new StringBuilder();
            c.getConges().forEach(s -> sb.append(s.getNom()+", "));

            SelectOption opt = SelectOption.of("Horaire "+(i+1), i+"").withDescription("Congés: "+sb.toString());
            if(i == currentCombinaison) opt = opt.withDefault(true);
            options.add(opt);
        }
        menu.addOptions(options);
        return menu.build();
    }


    private final HoraireActivite HORAIRE_MATIN = new HoraireActivite(6, 0, 12, 30, null);
    private final HoraireActivite HORAIRE_MIDI = new HoraireActivite(13, 0, 17, 30, null);
    private final HoraireActivite HORAIRE_SOIR = new HoraireActivite(18, 0, 23, 0, null);

    private static final String[] SYMBOLES_COURS = {":blue_square:", ":orange_square:", ":purple_square:", ":red_square:", ":yellow_square:", ":white_large_square:"};
    private static final String SYMBOLE_EMPTY = ":black_large_square:";

    private List<String> getFullCombinaisonString(CombinaisonHoraire combinaison) {

        int DEBUT_COURS = Integer.MAX_VALUE;
        int FIN_COURS = Integer.MIN_VALUE;

        for(Groupe g : combinaison.getGroupes()) {
            for(Activite a : g.getActivites()) {
                HoraireActivite h = a.getHoraire();
                DEBUT_COURS = Math.min(DEBUT_COURS, h.getHeureDepart());
                FIN_COURS = Math.max(FIN_COURS, h.getHeureFin());
            }
        }
        DEBUT_COURS = DEBUT_COURS/100 - (DEBUT_COURS % 100 == 0 ? 1 : 0);
        FIN_COURS = FIN_COURS / 100 + 1;

        List<String> messages = new ArrayList<>();
        int counter = 0;
        String[][] periodes = new String[7][(FIN_COURS-DEBUT_COURS)*2];

        StringBuilder sb = new StringBuilder();

        for(Groupe groupe : combinaison.getGroupes()) {
            String symbole = SYMBOLES_COURS[counter];
            sb.append(symbole+" "+groupe.toPrettyString());
            sb.append("\n");
            for(Activite activite : groupe.getActivites()) {

                int id = activite.getHoraire().getJour().getId();
                HoraireActivite horaire = activite.getHoraire();

                for(int i = 0; i<periodes[0].length/2; i++) {

                    HoraireActivite act1 = new HoraireActivite((i+DEBUT_COURS), 0, (i+DEBUT_COURS), 30, null);
                    HoraireActivite act2 = new HoraireActivite((i+DEBUT_COURS), 30, (i+DEBUT_COURS+1), 0, null);
                    if(horaire.overlapsWithIgnoreJour(act1)) {
                        periodes[id][i*2] = symbole;
                    }
                    if(horaire.overlapsWithIgnoreJour(act2)) {
                        periodes[id][i*2+1] = symbole;
                    }

                }


            }
            counter++;
        }

        for(int j = 0; j<periodes[0].length; j++) {


            for(int i = 0; i<periodes.length; i++) {
                String symbole = periodes[i][j];
                if(symbole == null) sb.append(SYMBOLE_EMPTY+SYMBOLE_EMPTY);
                else sb.append(symbole+symbole);
                sb.append(SYMBOLE_EMPTY);
            }

            if(j%2 == 0) {
                sb.append((j/2+DEBUT_COURS)+":00");
            } else {
                sb.append(((j/2)+DEBUT_COURS)+":30");
            }
            sb.append("\n");

            if(sb.length() > 1500) {
                messages.add(sb.toString());
                sb.setLength(0);
            }
        }
        if(sb.length() != 0) messages.add(sb.toString());

        return messages;
    }

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
