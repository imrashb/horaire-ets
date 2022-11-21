package me.imrashb.discord.commands;

import lombok.Data;
import me.imrashb.discord.commands.autocomplete.AutoCompleteStrategy;
import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.service.HorairETSService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class DiscordSlashCommand<Action extends DeferredAction> {

    private CommandDataImpl commandData;
    private HorairETSService mediatorService;
    private Map<String, AutoCompleteStrategy> autoCompleteStrategies;

    public DiscordSlashCommand(String name, String description, HorairETSService horairETSService) {
        this.mediatorService = horairETSService;
        this.autoCompleteStrategies = new HashMap<>();
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

    public DiscordSlashCommand addOption(OptionType type, String name, String description, boolean required, AutoCompleteStrategy strategy, Command.Choice... choices) {
        OptionData optionData = new OptionData(type, name, description, required, true).addChoices(choices);
        commandData.addOptions(optionData);
        this.addAutoCompleteStrategy(name, strategy);
        return this;
    }

    private void addAutoCompleteStrategy(String paramName, AutoCompleteStrategy strategy) {
        this.autoCompleteStrategies.put(paramName, strategy);
    }

    public List<Command.Choice> getAutoCompleteChoices(CommandAutoCompleteInteractionEvent event) {
        AutoCompleteStrategy strategy = autoCompleteStrategies.get(event.getFocusedOption().getName());
        if (strategy == null) return null;
        return strategy.getAutoCompleteChoices(event);
    }

    public abstract Action execute(SlashCommandInteractionEvent event);

    public void subscribeCommand(JDA jda) {
        jda.upsertCommand(commandData).complete();
    }

    public boolean isSlashCommandMatching(String name) {
        return name.equalsIgnoreCase(commandData.getName());
    }

}
