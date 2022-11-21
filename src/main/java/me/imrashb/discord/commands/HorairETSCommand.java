package me.imrashb.discord.commands;

import me.imrashb.discord.BotConstants;
import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.service.HorairETSService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Set;

public class HorairETSCommand extends DiscordSlashCommand<DeferredAction> {

    private final Set<DiscordSlashCommand> commands;

    public HorairETSCommand(HorairETSService coursManager, Set<DiscordSlashCommand> commands) {
        super("horairets", "Retourne la liste des commandes disponibles", coursManager);
        this.commands = commands;
    }

    @Override
    public DeferredAction execute(SlashCommandInteractionEvent event) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Utilisation de HorairÉTS");
        eb.setDescription("Les commandes disponibles par HorairÉTS se retrouvent ci-dessous");
        eb.setColor(BotConstants.EMBED_COLOR);
        for (DiscordSlashCommand c : commands) {
            eb.addField("/" + c.getCommandData().getName(), c.getCommandData().getDescription(), false);
        }
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        return null;
    }
}
