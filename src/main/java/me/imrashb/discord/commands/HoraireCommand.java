package me.imrashb.discord.commands;

import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.utils.MessageUtils;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.CombinaisonHoraireFactory;
import me.imrashb.domain.CoursManager;
import me.imrashb.exception.InvalidEncodedIdException;
import me.imrashb.utils.HoraireImageMaker;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;


public class HoraireCommand extends DiscordSlashCommand<DeferredAction>{

    private String ID_HORAIRE = "idhoraire";

    public HoraireCommand(CoursManager coursManager) {
        super("horaire", "Retourne l'image d'une combinaison d'horaire à partir d'un identifiant unique d'horaire", coursManager);

        this.addOption(OptionType.STRING, ID_HORAIRE, "L'identifiant unique de la combinaison d'horaire", true);
    }

    @Override
    public DeferredAction execute(SlashCommandInteractionEvent event) {

        String id = event.getOption(ID_HORAIRE).getAsString();

        try {
            CombinaisonHoraire comb = CombinaisonHoraireFactory.fromEncodedUniqueId(id, getCoursManager());

            MessageUtils.partagerImageHoraire(event, comb, HoraireImageMaker.LIGHT_THEME, null);
            event.reply("Voici la combinaison d'horaire relié à '"+comb.getUniqueId()+"'.")
                    .setEphemeral(true).queue();
        } catch(InvalidEncodedIdException e) {
            event.reply(e.getMessage())
                    .setEphemeral(true).queue();
        }

        return null;
    }
}
