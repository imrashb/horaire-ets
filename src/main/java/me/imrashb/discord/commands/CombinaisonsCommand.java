package me.imrashb.discord.commands;

import me.imrashb.discord.commands.autocomplete.AutoCompleteStrategy;
import me.imrashb.discord.commands.options.CommandOptionUtils;
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
    private final String ID_CONGE = "conge";
    private final String ID_SESSION;
    private final String ID_NB_COURS = "nombrecours";
    private final int NB_COURS_MAX = 10;
    private final int NB_CONGE_MAX = 6;

    public CombinaisonsCommand(CoursManager coursManager) {
        super("combinaisons", "Retourne les combinaisons d'horaires en fonction des cours en arguments", coursManager);


        List<Command.Choice> choicesSession = new ArrayList<>();

        for(String s : this.getCoursManager().getSessions()) {
            choicesSession.add(new Command.Choice(s, s));
        }

        this.ID_SESSION = new CommandOptionUtils().addSessionOption(this);

        this.addOption(OptionType.INTEGER,
                ID_NB_COURS,
                "Le nombre de cours par horaire générée",
                true);

        for (int i = 0; i < NB_COURS_MAX; i++) {
            boolean required = false;
            if (i == 0) required = true;
            String paramName = ID_COURS + (i+1);
            AutoCompleteStrategy strategy = event -> {
                if(event.getOption(ID_SESSION) == null) return null;

                String text = event.getFocusedOption().getValue();
                List<Cours> cours = coursManager.getListeCours(event.getOption(ID_SESSION).getAsString());
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

        List<Command.Choice> choicesConge = new ArrayList<>();

        for(Jour j : Jour.values()) {
            choicesConge.add(new Command.Choice(j.getNom(), j.getId()));
        }

        for (int i = 0; i < NB_CONGE_MAX; i++) {
            String paramName = ID_CONGE + (i+1);
            addOption(
                    OptionType.STRING, paramName, "Le "+(i+1)+"e congé de l'horaire", false, choicesConge.toArray(new Command.Choice[]{}));
        }
    }

    @Override
    public EmbedEditDeferredAction execute(SlashCommandInteractionEvent event) {
        List<String> cours = new ArrayList<>();
        String sessionId = event.getOption(ID_SESSION).getAsString();

        for (int i = 0; i < NB_COURS_MAX; i++) {
            OptionMapping mapping = event.getOption(ID_COURS + (i+1));
            if (mapping != null) {
                cours.add(mapping.getAsString());
            }
        }

        Set<Jour> conges = new HashSet<>();

        for (int i = 0; i < NB_CONGE_MAX; i++) {
            OptionMapping mapping = event.getOption(ID_CONGE + (i+1));
            if (mapping != null) {
                for(Jour j : Jour.values()) {
                    if(j.getId() == mapping.getAsInt()) {
                        conges.add(j);
                        break;
                    }
                }
            }
        }

        List<Cours> listeCours = this.getCoursManager().getListeCours(sessionId);

        if(listeCours == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("La session '"+sessionId+"' est invalide! ");
            sb.append("La dernière session est '"+this.getCoursManager().getDerniereSession()+"'. ");
            sb.append("Les sessions disponibles sont "+this.getCoursManager().getSessions().toString()+".");
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
            List<CombinaisonHoraire> combinaisons = new GenerateurHoraire(listeCours).getCombinaisonsHoraire(nbCours, conges, cours.toArray(new String[0]));

            if(combinaisons.size() == 0) {
                event.reply("Il n'y a aucune combinaison d'horaire possible avec les cours fournis.").setEphemeral(true).queue();
                return null;
            }

            CombinaisonsEmbed embed = new CombinaisonsEmbed(combinaisons, event.getUser(), sessionId, getCoursManager().getPreferencesUtilisateurService());
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
