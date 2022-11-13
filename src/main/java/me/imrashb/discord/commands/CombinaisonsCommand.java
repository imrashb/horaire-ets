package me.imrashb.discord.commands;

import me.imrashb.discord.embed.*;
import me.imrashb.discord.events.action.EmbedEditDeferredAction;
import me.imrashb.domain.*;
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
            this.addOption(OptionType.STRING, ID_COURS + (i+1), "Le " + (i+1) + "e cours à inclure dans l'horaire", required);
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

        if(listeCours.size() < nbCours) {
            StringBuilder sb = new StringBuilder();
            sb.append("Le nombre de cours '"+nbCours+"' est invalide! ");
            sb.append("Avec les cours choisis, le nombre de cours maximal est "+listeCours.size()+".");
            event.reply(sb.toString()).setEphemeral(true).queue();
            return null;
        }

        List<CombinaisonHoraire> combinaisons = new GenerateurHoraire(listeCours).getCombinaisonsHoraire(nbCours, cours.toArray(new String[0]));

        CombinaisonsEmbed embed = new CombinaisonsEmbed(combinaisons);
        embed.queueEmbed(event, true);
        List<User> users = new ArrayList<>();
        users.add(event.getUser());
        return new EmbedEditDeferredAction(users, embed);
    }

}
