package me.imrashb.discord.commands;

import me.imrashb.discord.commands.options.CommandOptionUtils;
import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.utils.MessageUtils;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.CombinaisonHoraireFactory;
import me.imrashb.domain.CoursManager;
import me.imrashb.domain.PreferencesUtilisateur;
import me.imrashb.exception.InvalidEncodedIdException;
import me.imrashb.utils.HoraireImageMaker;
import me.imrashb.utils.HoraireImageMakerTheme;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;


public class MonHoraireCommand extends DiscordSlashCommand<DeferredAction>{

    private final String ID_SESSION;

    public MonHoraireCommand(CoursManager coursManager) {
        super("monhoraire", "Retourne votre horaire sauvegardé pour une certaine session", coursManager);
        this.ID_SESSION = new CommandOptionUtils().addSessionOption(this);
    }

    @Override
    public DeferredAction execute(SlashCommandInteractionEvent event) {

        String sessionId = event.getOption(ID_SESSION).getAsString();

        try {

            PreferencesUtilisateur preferencesUtilisateur = getCoursManager().getPreferencesUtilisateurService().getPreferencesUtilisateur(event.getUser().getIdLong());
            if(preferencesUtilisateur == null) {
                event.reply("Vous n'avez pas encore sauvegardé d'horaire pour des sessions.").setEphemeral(true).queue();
                return null;
            }

            String idHoraire = preferencesUtilisateur.getHoraires().get(sessionId);

            if(idHoraire == null) {
                event.reply("Vous n'avez pas encore sauvegardé d'horaire pour la session "+sessionId+".").setEphemeral(true).queue();
                return null;
            }

            CombinaisonHoraire comb = CombinaisonHoraireFactory.fromEncodedUniqueId(idHoraire, getCoursManager());

            PreferencesUtilisateur pref = getCoursManager().getPreferencesUtilisateurService().getPreferencesUtilisateur(event.getUser().getIdLong());
            HoraireImageMakerTheme theme = HoraireImageMaker.getThemeFromId(pref.getThemeId());

            MessageUtils.partagerImageHoraire(event, comb, theme, null);
            event.reply("Voici votre horaire pour la session '"+sessionId+"'.")
                    .setEphemeral(true).queue();
        } catch(InvalidEncodedIdException e) {
            event.reply(e.getMessage())
                    .setEphemeral(true).queue();
        }

        return null;
    }
}
