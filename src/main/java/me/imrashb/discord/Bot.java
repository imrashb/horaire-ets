package me.imrashb.discord;


import me.imrashb.discord.button.*;
import me.imrashb.discord.commands.*;
import me.imrashb.discord.events.*;
import me.imrashb.domain.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.*;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.cache.*;

import java.util.*;

public class Bot {

    private CoursManager coursManager;
    private JDA jda;
    private Set<DiscordSlashCommand> commands;
    Bot(String token, CoursManager coursManager) throws InterruptedException {
        this.coursManager = coursManager;
        this.commands = new HashSet<>();

        JDABuilder builder = JDABuilder.createDefault(token);

        if(!this.coursManager.isReady()) {
            this.coursManager.addCoursManagerReadyListener(ready -> {
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
        jda.getPresence().setPresence(Activity.watching("les combinaisons d'horaires"), true);

        this.subscribeCommands();
        this.subscribeListeners();
    }

    private void subscribeListeners() {
        jda.addEventListener(new SlashCommandInteractionEventHandler(commands));
        jda.addEventListener(new CommandAutoCompleteInteractionEventHandler(commands));
        jda.addEventListener(new ButtonInteractionEventHandler());
    }

    private void subscribeCommands() {

        // Update commands
        this.jda.updateCommands().complete();

        this.commands.add(new CombinaisonsCommand(coursManager));

        for(DiscordSlashCommand c : this.commands) {
            c.subscribeCommand(jda);
        }
    }

}
