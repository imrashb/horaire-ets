package me.imrashb.discord.commands.options;

import me.imrashb.discord.commands.DiscordSlashCommand;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandOptionUtils {

    public String addSessionOption(DiscordSlashCommand command) {
        List<Command.Choice> choicesSession = new ArrayList<>();

        for (String s : command.getMediatorService().getSessionService().getSessions()) {
            choicesSession.add(new Command.Choice(s, s));
        }

        String ID_SESSION = "session";
        OptionData opt = new OptionData(
                OptionType.STRING,
                ID_SESSION,
                "La session dans laquelle les combinaisons d'horaires seront générées",
                true
        ).addChoices(choicesSession.toArray(new Command.Choice[]{}));
        command.getCommandData().addOptions(opt);
        return ID_SESSION;
    }

}
