package me.imrashb.discord.commands;

import me.imrashb.discord.commands.autocomplete.AutoCompleteStrategy;
import me.imrashb.discord.commands.options.CommandOptionUtils;
import me.imrashb.discord.embed.combinaisons.CombinaisonsEmbed;
import me.imrashb.discord.events.action.EmbedEditDeferredAction;
import me.imrashb.discord.utils.DomainUser;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Jour;
import me.imrashb.exception.CoursAlreadyPresentException;
import me.imrashb.exception.CoursDoesntExistException;
import me.imrashb.service.HorairETSService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinaisonsCommand extends DiscordSlashCommand<EmbedEditDeferredAction> {

    private final String ID_COURS = "cours";
    private final String ID_CONGE = "conge";
    private final String ID_SESSION;
    private final String ID_NB_COURS = "nombrecours";
    private final int NB_COURS_MAX = 10;
    private final int NB_CONGE_MAX = 6;

    public CombinaisonsCommand(HorairETSService mediator) {
        super("combinaisons", "Retourne les combinaisons d'horaires en fonction des cours en arguments", mediator);


        List<Command.Choice> choicesSession = new ArrayList<>();

        for (String s : this.getMediatorService().getCoursService().getSessions()) {
            choicesSession.add(new Command.Choice(s, s));
        }

        this.ID_SESSION = new CommandOptionUtils().addSessionOption(this);

        this.addOption(OptionType.INTEGER,
                ID_NB_COURS,
                "Le nombre de cours par horaire générée",
                true);

        for (int i = 0; i < NB_COURS_MAX; i++) {
            boolean required = i == 0;
            String paramName = ID_COURS + (i + 1);
            AutoCompleteStrategy strategy = event -> {
                if (event.getOption(ID_SESSION) == null) return null;

                String text = event.getFocusedOption().getValue();
                List<Cours> cours = getMediatorService().getCoursService().getListeCours(event.getOption(ID_SESSION).getAsString());
                if (cours == null) return null;

                List<Command.Choice> liste = new ArrayList<>();
                for (Cours c : cours) {
                    String sigle = c.getSigle();
                    if (sigle.toLowerCase().contains(text.toLowerCase())) {
                        liste.add(new Command.Choice(sigle, sigle));
                        if (liste.size() == AutoCompleteStrategy.NOMBRE_MAX_CHOIX) break;
                    }
                }
                return liste;
            };
            this.addOption(OptionType.STRING, paramName, "Le " + (i + 1) + "e cours à inclure dans l'horaire", required, strategy);

        }

        List<Command.Choice> choicesConge = new ArrayList<>();

        for (Jour j : Jour.values()) {
            choicesConge.add(new Command.Choice(j.getNom(), j.getId()));
        }

        for (int i = 0; i < NB_CONGE_MAX; i++) {
            String paramName = ID_CONGE + (i + 1);
            addOption(
                    OptionType.STRING, paramName, "Le " + (i + 1) + "e congé de l'horaire", false, choicesConge.toArray(new Command.Choice[]{}));
        }
    }

    @Override
    public EmbedEditDeferredAction execute(SlashCommandInteractionEvent event) {
        List<String> cours = new ArrayList<>();
        String sessionId = event.getOption(ID_SESSION).getAsString();

        for (int i = 0; i < NB_COURS_MAX; i++) {
            OptionMapping mapping = event.getOption(ID_COURS + (i + 1));
            if (mapping != null) {
                cours.add(mapping.getAsString());
            }
        }

        Set<Jour> conges = new HashSet<>();

        for (int i = 0; i < NB_CONGE_MAX; i++) {
            OptionMapping mapping = event.getOption(ID_CONGE + (i + 1));
            if (mapping != null) {
                for (Jour j : Jour.values()) {
                    if (j.getId() == mapping.getAsInt()) {
                        conges.add(j);
                        break;
                    }
                }
            }
        }

        List<Cours> listeCours = this.getMediatorService().getCoursService().getListeCours(sessionId);

        if (listeCours == null) {
            String sb = "La session '" + sessionId + "' est invalide! " +
                    "La dernière session est '" + this.getMediatorService().getCoursService().getDerniereSession() + "'. " +
                    "Les sessions disponibles sont " + this.getMediatorService().getCoursService().getSessions().toString() + ".";
            event.reply(sb).setEphemeral(true).queue();
            return null;
        }

        int nbCours = event.getOption(ID_NB_COURS).getAsInt();
        if (cours.size() < nbCours) {
            String sb = "Le nombre de cours '" + nbCours + "' est invalide! " +
                    "Avec les cours choisis, le nombre de cours maximal est " + cours.size() + ".";
            event.reply(sb).setEphemeral(true).queue();
            return null;
        }

        try {
            List<CombinaisonHoraire> combinaisons = getMediatorService().getCombinaisonService()
                    .getCombinaisonsHoraire(cours.toArray(new String[0]), conges.toArray(new Jour[0]), sessionId, nbCours);

            if (combinaisons.size() == 0) {
                event.reply("Il n'y a aucune combinaison d'horaire possible avec les cours fournis.").setEphemeral(true).queue();
                return null;
            }

            CombinaisonsEmbed embed = new CombinaisonsEmbed(combinaisons, sessionId, new DomainUser(event.getUser(), getMediatorService()));
            embed.queueEmbed(event, true);
            List<User> users = new ArrayList<>();
            users.add(event.getUser());
            return new EmbedEditDeferredAction(users, embed);
        } catch (CoursDoesntExistException | CoursAlreadyPresentException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return null;

        }

    }

}
