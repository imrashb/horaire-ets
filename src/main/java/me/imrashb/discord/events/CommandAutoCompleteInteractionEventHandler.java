package me.imrashb.discord.events;

import lombok.*;
import me.imrashb.discord.commands.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.hooks.*;

import java.util.*;

@AllArgsConstructor
public class CommandAutoCompleteInteractionEventHandler extends ListenerAdapter {

    @NonNull
    private Set<DiscordSlashCommand> commands;

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {

        final String commandName = event.getName();

        for(DiscordSlashCommand command : commands) {
            if(command.isSlashCommandMatching(commandName)) {
                String autoComplete = command.getAutoCompleteText(event.getFocusedOption().getName());
                System.out.println(autoComplete);
                event.replyChoice(event.getFocusedOption().getName(), autoComplete).queue();
                break;
            }
        }

    }

}
