package me.imrashb.discord.commands;

import me.imrashb.discord.commands.options.CommandOptionUtils;
import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.utils.MessageUtils;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.PreferencesUtilisateur;
import me.imrashb.exception.InvalidEncodedIdException;
import me.imrashb.service.HorairETSService;
import me.imrashb.utils.HoraireImageMaker;
import me.imrashb.utils.HoraireImageMakerTheme;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;


public class HoraireCommand extends DiscordSlashCommand<DeferredAction> {

    private final String ID_UTILISATEUR = "utilisateur";
    private final String ID_SESSION;

    public HoraireCommand(HorairETSService coursManager) {
        super("horaire", "Retourne l'horaire d'un utilisateur", coursManager);
        this.ID_SESSION = new CommandOptionUtils().addSessionOption(this);
        this.addOption(OptionType.MENTIONABLE, ID_UTILISATEUR, "L'utilisateur a qui on veut récupérer l'horaire", false);
    }

    @Override
    public DeferredAction execute(SlashCommandInteractionEvent event) {

        OptionMapping mapping = event.getOption(ID_UTILISATEUR);

        User user = event.getUser();

        if (mapping != null) user = mapping.getAsUser();

        String sessionId = event.getOption(ID_SESSION).getAsString();
        try {
            PreferencesUtilisateur preferencesUtilisateur = getMediatorService().getPreferencesService().getPreferencesUtilisateur(user.getIdLong());

            if (user != event.getUser()) {
                if (preferencesUtilisateur.isPrivate()) {
                    event.reply(user.getName() + " n'a pas activé le partage d'horaire. Si vous voulez voir son horaire, veuillez lui demander.").setEphemeral(true).queue();
                    return null;
                }
            }

            String idHoraire = preferencesUtilisateur.getHoraires().get(sessionId);
            if (idHoraire == null) {
                StringBuilder sb = new StringBuilder();
                if (user == event.getUser()) {
                    sb.append("Vous n'avez ");
                } else {
                    sb.append(user.getName()).append(" n'a ");
                }
                sb.append("pas encore sauvegardé d'horaire pour la session ").append(sessionId).append(".");
                event.reply(sb.toString()).setEphemeral(true).queue();
                return null;
            }

            CombinaisonHoraire comb = getMediatorService().getCombinaisonService().getCombinaisonFromEncodedId(idHoraire);
            HoraireImageMakerTheme theme = HoraireImageMaker.getThemeFromId(preferencesUtilisateur.getThemeId());

            Image img = new HoraireImageMaker(comb, theme).drawHoraire();
            FileUpload file = MessageUtils.getFileUploadFromImage(img, comb.getUniqueId() + ".jpeg");

            event.replyFiles(file).mention(event.getUser()).setEphemeral(true).queue();
        } catch (InvalidEncodedIdException e) {
            event.reply(e.getMessage())
                    .setEphemeral(true).queue();
        }
        return null;
    }
}
