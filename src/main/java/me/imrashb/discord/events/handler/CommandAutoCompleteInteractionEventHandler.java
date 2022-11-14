package me.imrashb.discord.events.handler;

import lombok.*;
import me.imrashb.discord.commands.*;
import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.events.handler.InteractionHandler;
import me.imrashb.discord.events.handler.SlashCommandInteractionEventHandler;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.*;

public class CommandAutoCompleteInteractionEventHandler extends InteractionHandler<CommandAutoCompleteInteractionEvent, DeferredAction> {

    @NonNull
    private Set<DiscordSlashCommand> commands;

    public CommandAutoCompleteInteractionEventHandler(Set<DiscordSlashCommand> commands) {
        super(true);
        this.commands = commands;
    }

    @Override
    protected DeferredAction processInteraction(CommandAutoCompleteInteractionEvent event, DeferredAction action) {

        final String commandName = event.getName();
        for(DiscordSlashCommand command : commands) {
            if(command.isSlashCommandMatching(commandName)) {
                List<Command.Choice> choices = command.getAutoCompleteChoices(event);
                if(choices != null) event.replyChoices(choices).queue();
                break;
            }
        }

        return null;
    }
}
