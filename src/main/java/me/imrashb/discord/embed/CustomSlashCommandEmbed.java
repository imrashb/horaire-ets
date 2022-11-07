package me.imrashb.discord.embed;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.interactions.*;
import net.dv8tion.jda.api.interactions.components.*;

import java.util.*;
import java.util.concurrent.*;

public abstract class CustomSlashCommandEmbed {

    protected EmbedBuilder embedBuilder = new EmbedBuilder();

    private ScheduledFuture<?> scheduledFuture = null;

    @Getter
    private InteractionHook hook;

    @Getter
    private Long messageId;

    private long alive = 60;


    @Getter @Setter
    private boolean stayAlive = false;

    private EmbedLayout layout;

    public boolean getStayAlive() {
        return this.stayAlive;
    }

    public final void queueEmbed(SlashCommandInteractionEvent event, boolean ephemeral) {
        layout = this.buildLayout();

        event.replyEmbeds(this.update().build()).setComponents(layout.getRows()).setEphemeral(ephemeral).timeout(alive, TimeUnit.SECONDS).queue((hook) -> {
            this.hook = hook;
            hook.retrieveOriginal().queue(message -> {
                this.messageId = message.getIdLong();
            });
            this.deleteAfterInactivity();
        });

    }

    protected abstract EmbedBuilder update();

    public void fireUpdate(GenericComponentInteractionCreateEvent event) {

        layout.update(event);

        System.out.println("hereasdasd");
        if(this.scheduledFuture != null && (this.scheduledFuture.isDone() && !this.scheduledFuture.cancel(false))) {
            return;
        }
        System.out.println(layout.getRows());

        hook
                .editOriginalEmbeds(this.update().build())
                .setComponents(layout.getRows())
                .timeout(alive, TimeUnit.SECONDS)
                .queue();
        event.deferEdit().queue();
        this.deleteAfterInactivity();
    }

    private void deleteAfterInactivity() {
        if(!stayAlive)
            this.scheduledFuture = hook.deleteOriginal().queueAfter(alive, TimeUnit.SECONDS);
    }

    protected abstract EmbedLayout buildLayout();



}
