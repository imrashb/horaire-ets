package me.imrashb.discord.commands;

import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.service.HorairETSService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SessionsCommand extends DiscordSlashCommand<DeferredAction>{
    public SessionsCommand(HorairETSService coursManager) {
        super("sessions", "Retourne les sessions supportées en ce moment", coursManager);
    }

    @Override
    public DeferredAction execute(SlashCommandInteractionEvent event) {

        if(getMediatorService().getCoursService().getSessions().size() == 0) {
            event.reply("Aucune session est supportée pour le moment. Veuillez réessayer.")
                    .setEphemeral(true).queue();
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Voici les sessions supportées en ce moment: ");

        for(String s : getMediatorService().getCoursService().getSessions()) {
            sb.append(s).append(", ");
        }
        event.reply(sb.substring(0, sb.lastIndexOf(",")))
                .setEphemeral(true).queue();

        return null;
    }
}
