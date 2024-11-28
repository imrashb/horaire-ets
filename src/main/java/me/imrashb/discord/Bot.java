package me.imrashb.discord;


import me.imrashb.discord.commands.*;
import me.imrashb.discord.events.controller.InteractionHandlerController;
import me.imrashb.discord.events.handler.CommandAutoCompleteInteractionEventHandler;
import me.imrashb.discord.events.handler.ComponentControlledEmbedHandler;
import me.imrashb.discord.events.handler.SlashCommandInteractionEventHandler;
import me.imrashb.discord.routines.*;
import me.imrashb.service.HorairETSService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.*;

public class Bot {

    private final HorairETSService mediator;
    private final Set<DiscordSlashCommand> commands;
    private JDA jda;

    Bot(String token, HorairETSService mediator) throws InterruptedException {
        this.mediator = mediator;
        this.commands = new HashSet<>();

        JDABuilder builder = JDABuilder.createLight(token);

        if (!this.mediator.getSessionService().isReady()) {
            this.mediator.getSessionService().addSessionManagerReadyListener(ready -> {
                try {
                    if (Bot.this.jda == null && ready) {
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
        jdaBuilder.disableCache(Arrays.asList(CacheFlag.values())); // Disable useless caches
        jdaBuilder.setChunkingFilter(ChunkingFilter.NONE);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.NONE); // Don't cache users
        jdaBuilder.disableIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.SCHEDULED_EVENTS);
        jdaBuilder.setLargeThreshold(50);

        try {
            this.jda = jdaBuilder.build().awaitReady();
        } catch(Exception e) {
            System.err.println("Failed to initialize Discord bot. Reason: "+e.getMessage());
            e.printStackTrace();
            return;
        }

        // Presence Routine
        new DiscordPresenceRoutine(this.jda).startRoutine();

        this.subscribeCommands();
        this.subscribeListeners();
    }

    private void subscribeListeners() {

        InteractionHandlerController interactionHandlerController = new InteractionHandlerController(jda);
        interactionHandlerController.addInteractionHandler(new SlashCommandInteractionEventHandler(this.commands));
        interactionHandlerController.addInteractionHandler(new CommandAutoCompleteInteractionEventHandler(this.commands));
        interactionHandlerController.addInteractionHandler(new ComponentControlledEmbedHandler());
        this.jda.addEventListener(interactionHandlerController);
    }

    private void subscribeCommands() {

        // Update commands
        this.jda.updateCommands().complete();

        this.commands.add(new CombinaisonsCommand(mediator));
        this.commands.add(new SessionsCommand(mediator));
        this.commands.add(new HorairETSCommand(mediator, this.commands));
        this.commands.add(new HoraireCommand(mediator));
        for (DiscordSlashCommand c : this.commands) {
            c.subscribeCommand(jda);
        }
    }

}
