package me.imrashb.discord.commands;

import me.imrashb.discord.commands.autocomplete.AutoCompleteStrategy;
import me.imrashb.discord.embed.combinaisons.CombinaisonsEmbed;
import me.imrashb.discord.events.action.EmbedEditDeferredAction;
import me.imrashb.domain.*;
import me.imrashb.exception.*;
import me.imrashb.parser.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.commands.*;

import java.util.*;

public class CombinaisonsCommand extends DiscordSlashCommand<EmbedEditDeferredAction> {

    private final String ID_COURS = "cours";
    private final String ID_TRIMESTRE = "trimestre";
    private final String ID_NB_COURS = "nombrecours";
    private final int NB_COURS_MAX = 6;

    public CombinaisonsCommand(CoursManager coursManager) {
        super("combinaisons", "Retourne les combinaisons d'horaires en fonction des cours en arguments", coursManager);


        List<Command.Choice> choicesTrimestre = new ArrayList<>();

        for(String s : this.getCoursManager().getTrimestres()) {
            choicesTrimestre.add(new Command.Choice(s, s));
        }

        this.addOption(
                OptionType.STRING,
                ID_TRIMESTRE,
                "Le trimestre dans lequel les combinaisons d'horaires seront générées",
                true,
                choicesTrimestre.toArray(new Command.Choice[]{})
                );

        this.addOption(OptionType.INTEGER,
                ID_NB_COURS,
                "Le nombre de cours par horaire générée",
                true);

        for (int i = 0; i < NB_COURS_MAX; i++) {
            boolean required = false;
            if (i == 0) required = true;
            String paramName = ID_COURS + (i+1);
            AutoCompleteStrategy strategy = event -> {
                if(event.getOption(ID_TRIMESTRE) == null) return null;

                String text = event.getFocusedOption().getValue();
                List<Cours> cours = coursManager.getListeCours(event.getOption(ID_TRIMESTRE).getAsString());
                if(cours == null) return null;

                List<Command.Choice> liste = new ArrayList<>();
                for(Cours c : cours) {
                    String sigle = c.getSigle();
                    if(sigle.toLowerCase().contains(text.toLowerCase())) {
                        liste.add(new Command.Choice(sigle, sigle));
                        if(liste.size() == AutoCompleteStrategy.NOMBRE_MAX_CHOIX) break;
                    }
                }
                return liste;
            };
            this.addOption(OptionType.STRING, paramName, "Le " + (i+1) + "e cours à inclure dans l'horaire", required, strategy);

        }
    }

    @Override
    public EmbedEditDeferredAction execute(SlashCommandInteractionEvent event) {
        List<String> cours = new ArrayList<>();
        String trimestre = event.getOption(ID_TRIMESTRE).getAsString();

        for (int i = 0; i < NB_COURS_MAX; i++) {
            OptionMapping mapping = event.getOption(ID_COURS + (i+1));
            if (mapping != null) {
                cours.add(mapping.getAsString());
            }
        }

        List<Cours> listeCours = this.getCoursManager().getListeCours(trimestre);

        if(listeCours == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Le trimestre '"+trimestre+"' est invalide! ");
            sb.append("Le dernier trimestre est '"+this.getCoursManager().getDernierTrimestre()+"'. ");
            sb.append("Les trimestres disponibles sont "+this.getCoursManager().getTrimestres().toString()+".");
            event.reply(sb.toString()).setEphemeral(true).queue();
            return null;
        }

        int nbCours = event.getOption(ID_NB_COURS).getAsInt();
        if(cours.size() < nbCours) {
            StringBuilder sb = new StringBuilder();
            sb.append("Le nombre de cours '"+nbCours+"' est invalide! ");
            sb.append("Avec les cours choisis, le nombre de cours maximal est "+cours.size()+".");
            event.reply(sb.toString()).setEphemeral(true).queue();
            return null;
        }

        try {
            List<CombinaisonHoraire> combinaisons = new GenerateurHoraire(listeCours).getCombinaisonsHoraire(nbCours, cours.toArray(new String[0]));

            if(combinaisons.size() == 0) {
                event.reply("Il n'y a aucune combinaison d'horaire possible avec les cours fournis.").queue();
                return null;
            }

            CombinaisonsEmbed embed = new CombinaisonsEmbed(combinaisons);
            embed.queueEmbed(event, true);
            List<User> users = new ArrayList<>();
            users.add(event.getUser());
            return new EmbedEditDeferredAction(users, embed);
        } catch(CoursDoesntExistException | CoursAlreadyPresentException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return null;

        }

    }

}
