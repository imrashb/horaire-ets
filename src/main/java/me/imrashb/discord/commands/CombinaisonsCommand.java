package me.imrashb.discord.commands;

import me.imrashb.discord.button.*;
import me.imrashb.discord.embed.*;
import me.imrashb.domain.*;
import me.imrashb.parser.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.commands.*;

import java.util.*;

public class CombinaisonsCommand extends DiscordSlashCommand {

    private final String ID_COURS = "cours";
    private final String ID_TRIMESTRE = "trimestre";
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

        for (int i = 0; i < NB_COURS_MAX; i++) {
            boolean required = false;
            if (i == 0) required = true;
            this.addOption(OptionType.STRING, ID_COURS + (i+1), "Le " + (i+1) + "e cours à inclure dans l'horaire", required);
        }
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
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
        }

        List<CombinaisonHoraire> combinaisons = new GenerateurHoraire(listeCours).getCombinaisonsHoraire(cours.toArray(new String[0]));

        new CombinaisonsEmbed(combinaisons).queueEmbed(event, true);

    }

}
