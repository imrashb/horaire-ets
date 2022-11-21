package me.imrashb.discord.events.handler;

import lombok.NonNull;
import me.imrashb.discord.commands.DiscordSlashCommand;
import me.imrashb.discord.events.action.DeferredAction;
import net.dv8tion.jda.api.events.interaction.GenericAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Set;

public class SlashCommandInteractionEventHandler<C extends GenericAutoCompleteInteractionEvent, D> extends InteractionHandler<SlashCommandInteractionEvent, DeferredAction> {

    @NonNull
    private Set<DiscordSlashCommand> commands;

    public SlashCommandInteractionEventHandler(Set<DiscordSlashCommand> commands) {
        super(true);
        this.commands = commands;
    }

    @Override
    protected DeferredAction processInteraction(SlashCommandInteractionEvent interaction, DeferredAction action) {
        final String commandName = interaction.getName();

        for (DiscordSlashCommand command : commands) {
            if (command.isSlashCommandMatching(commandName)) {
                return command.execute(interaction);
            }
        }
        return null;
    }
}
