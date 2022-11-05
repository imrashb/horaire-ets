package me.imrashb.discord.embed;

import me.imrashb.discord.button.*;
import me.imrashb.discord.events.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.interactions.*;
import net.dv8tion.jda.api.interactions.components.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class CustomSlashCommandEmbed {

    protected EmbedBuilder embedBuilder = new EmbedBuilder();

    private InteractionHook hook;

    private long alive = 30;

    public final void queueEmbed(SlashCommandInteractionEvent event, boolean ephemereal) {
        List<ReactiveButton> buttons = this.buildReactiveButtons();
        ButtonInteractionEventHandler.buttons.addAll(buttons);

        List<ItemComponent> comp = new ArrayList<ItemComponent>();
        for(ReactiveButton b : buttons) comp.add(b.getComponent());

        event.replyEmbeds(this.update().build()).addActionRow(comp).setEphemeral(ephemereal).timeout(alive, TimeUnit.SECONDS).queue(hook -> {
            this.hook = hook;
        });
    }

    protected abstract EmbedBuilder update();

    public void fireUpdate(ButtonInteractionEvent event) {
        hook.editOriginalEmbeds(this.update().build()).timeout(alive, TimeUnit.SECONDS).queue();
        event.deferEdit().queue();
    }

    protected abstract List<ReactiveButton> buildReactiveButtons();

}
