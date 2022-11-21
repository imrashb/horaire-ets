package me.imrashb.discord.events.handler;

import lombok.NonNull;
import me.imrashb.discord.commands.DiscordSlashCommand;
import me.imrashb.discord.events.action.DeferredAction;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.Set;

public class CommandAutoCompleteInteractionEventHandler extends InteractionHandler<CommandAutoCompleteInteractionEvent, DeferredAction> {

    @NonNull
    private final Set<DiscordSlashCommand> commands;

    public CommandAutoCompleteInteractionEventHandler(Set<DiscordSlashCommand> commands) {
        super(true);
        this.commands = commands;
    }

    @Override
    protected DeferredAction processInteraction(CommandAutoCompleteInteractionEvent event, DeferredAction action) {

        final String commandName = event.getName();
        for (DiscordSlashCommand command : commands) {
            if (command.isSlashCommandMatching(commandName)) {
                List<Command.Choice> choices = command.getAutoCompleteChoices(event);
                if (choices != null) event.replyChoices(choices).queue();
                break;
            }
        }

        return null;
    }
}
