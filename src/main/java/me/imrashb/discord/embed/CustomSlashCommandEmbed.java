package me.imrashb.discord.embed;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.interactions.*;

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
    private List<EmbedListener> listeners = new ArrayList<EmbedListener>();

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

    public void addEmbedListener(EmbedListener listener) {
        this.listeners.add(listener);
    }

    protected abstract EmbedBuilder update();

    public void fireUpdate(GenericComponentInteractionCreateEvent event) {

        layout.update(event);

        if(this.scheduledFuture != null && (!this.scheduledFuture.cancel(false) && this.scheduledFuture.isDone())) {
            return;
        }

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
            this.scheduledFuture = hook.deleteOriginal().queueAfter(alive, TimeUnit.SECONDS, delete -> {
                for(EmbedListener listener : listeners) {
                    listener.onEmbedDelete();
                }
            });
    }

    protected abstract EmbedLayout buildLayout();



}
