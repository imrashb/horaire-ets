package me.imrashb.discord.events;

import lombok.*;
import me.imrashb.discord.commands.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.hooks.*;

import java.util.*;

@AllArgsConstructor
public class SlashCommandInteractionEventHandler extends ListenerAdapter {

    @NonNull
    private Set<DiscordSlashCommand> commands;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        final String commandName = event.getName();

        for(DiscordSlashCommand command : commands) {
            if(command.isSlashCommandMatching(commandName)) {
                command.execute(event);
                break;
            }
        }

    }

}
