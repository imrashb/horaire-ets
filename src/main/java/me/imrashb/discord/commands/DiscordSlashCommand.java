package me.imrashb.discord.commands;

import lombok.*;
import me.imrashb.domain.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.internal.interactions.*;

import java.util.*;

@Data
public abstract class DiscordSlashCommand {

    private CommandDataImpl commandData;

    private Map<String, String> autoCompletes;

    private CoursManager coursManager;

    public DiscordSlashCommand(String name, String description, CoursManager coursManager) {
        this.coursManager = coursManager;
        this.autoCompletes = new HashMap<>();
        commandData = new CommandDataImpl(name, description);
        commandData.setGuildOnly(false);
    }

    public DiscordSlashCommand addOption(OptionType type, String name, String description, boolean required) {
        commandData.addOption(type, name, description, required);
        return this;
    }

    public DiscordSlashCommand addOption(OptionType type, String name, String description, boolean required, Command.Choice... choices) {
        OptionData optionData = new OptionData(type, name, description, required).addChoices(choices);
        commandData.addOptions(optionData);
        return this;
    }

    public String getAutoCompleteText(String commandName) {
        return autoCompletes.get(commandName);
    }


    public abstract void execute(SlashCommandInteractionEvent event);

    public void subscribeCommand(JDA jda) {
        jda.upsertCommand(commandData).complete();

    }

    public boolean isSlashCommandMatching(String name) {
        return name.equalsIgnoreCase(commandData.getName());
    }

}
