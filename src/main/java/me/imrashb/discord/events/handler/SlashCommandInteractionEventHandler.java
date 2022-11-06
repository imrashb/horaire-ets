package me.imrashb.discord.events.handler;

import lombok.*;
import me.imrashb.discord.commands.*;
import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.events.handler.InteractionHandler;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.hooks.*;

import java.util.*;

public class SlashCommandInteractionEventHandler extends InteractionHandler<SlashCommandInteractionEvent, DeferredAction> {

    @NonNull
    private Set<DiscordSlashCommand> commands;

    public SlashCommandInteractionEventHandler(Set<DiscordSlashCommand> commands) {
        super(true);
        this.commands = commands;
    }

    @Override
    protected DeferredAction processInteraction(SlashCommandInteractionEvent interaction, DeferredAction action) {
        final String commandName = interaction.getName();

        for(DiscordSlashCommand command : commands) {
            if(command.isSlashCommandMatching(commandName)) {
                return command.execute(interaction);
            }
        }
        return null;
    }
}
