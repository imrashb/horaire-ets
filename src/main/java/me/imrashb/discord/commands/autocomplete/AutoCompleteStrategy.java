package me.imrashb.discord.commands.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public interface AutoCompleteStrategy {

    int NOMBRE_MAX_CHOIX = 25;

    List<Command.Choice> getAutoCompleteChoices(CommandAutoCompleteInteractionEvent event);

}
