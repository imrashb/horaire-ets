package me.imrashb.discord;


import me.imrashb.discord.commands.*;
import me.imrashb.discord.events.controller.InteractionHandlerController;
import me.imrashb.discord.events.handler.CommandAutoCompleteInteractionEventHandler;
import me.imrashb.discord.events.handler.ComponentControlledEmbedHandler;
import me.imrashb.discord.events.handler.SlashCommandInteractionEventHandler;
import me.imrashb.service.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.*;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.cache.*;

import java.util.*;

public class Bot {

    private HorairETSService mediator;
    private JDA jda;
    private Set<DiscordSlashCommand> commands;
    private InteractionHandlerController interactionHandlerController;
    Bot(String token, HorairETSService mediator) throws InterruptedException {
        this.mediator = mediator;
        this.commands = new HashSet<>();

        JDABuilder builder = JDABuilder.createDefault(token);

        if(!this.mediator.getCoursService().isReady()) {
            this.mediator.getCoursService().addCoursManagerReadyListener(ready -> {
                try {
                    if(Bot.this.jda == null && ready) {
                        Bot.this.configure(builder);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            this.configure(builder);
        }

    }

    private void configure(JDABuilder jdaBuilder) throws InterruptedException {
        // Memory usage config
        jdaBuilder.disableCache(CacheFlag.ACTIVITY);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ONLINE);
        jdaBuilder.setChunkingFilter(ChunkingFilter.NONE);
        jdaBuilder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
        jdaBuilder.setLargeThreshold(50);

        this.jda = jdaBuilder.build().awaitReady();

        // Presence
        jda.getPresence().setPresence(Activity.listening("/horairets"), true);

        this.subscribeCommands();
        this.subscribeListeners();
    }

    private void subscribeListeners() {

        this.interactionHandlerController = new InteractionHandlerController(jda);
        interactionHandlerController.addInteractionHandler(new SlashCommandInteractionEventHandler(this.commands));
        interactionHandlerController.addInteractionHandler(new CommandAutoCompleteInteractionEventHandler(this.commands));
        interactionHandlerController.addInteractionHandler(new ComponentControlledEmbedHandler());
        this.jda.addEventListener(this.interactionHandlerController);
    }

    private void subscribeCommands() {

        // Update commands
        this.jda.updateCommands().complete();

        this.commands.add(new CombinaisonsCommand(mediator));
        this.commands.add(new SessionsCommand(mediator));
        this.commands.add(new HorairETSCommand(mediator, this.commands));
        this.commands.add(new HoraireCommand(mediator));
        for(DiscordSlashCommand c : this.commands) {
            c.subscribeCommand(jda);
        }
    }

}
