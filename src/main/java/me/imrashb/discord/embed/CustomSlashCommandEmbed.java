package me.imrashb.discord.embed;

import lombok.Getter;
import me.imrashb.discord.utils.DomainUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class CustomSlashCommandEmbed {

    protected final EmbedBuilder embedBuilder = new EmbedBuilder();
    private final long alive = 60;
    private final List<EmbedListener> listeners = new ArrayList<>();
    @Getter
    private final DomainUser user;
    private final boolean withComponents;
    private ScheduledFuture<?> scheduledFuture = null;
    @Getter
    private InteractionHook hook;
    @Getter
    private Long messageId;
    private EmbedLayout layout = null;

    public CustomSlashCommandEmbed(DomainUser user, boolean withComponents) {
        this.user = user;
        this.withComponents = withComponents;
    }

    public final void queueEmbed(SlashCommandInteractionEvent event, boolean ephemeral) {
        if (withComponents) {
            layout = this.buildLayout();
        }

        ReplyCallbackAction cb = event.replyEmbeds(this.update().build());

        if (layout != null) {
            cb = cb.setComponents(layout.getRows());
        }

        cb.setEphemeral(ephemeral).timeout(alive, TimeUnit.SECONDS).queue((hook) -> {
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
        layout.update(event, user);
        if (this.scheduledFuture != null && (!this.scheduledFuture.cancel(false) && this.scheduledFuture.isDone())) {
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
        this.scheduledFuture = hook.deleteOriginal().queueAfter(alive, TimeUnit.SECONDS, delete -> {
            for (EmbedListener listener : listeners) {
                listener.onEmbedDelete();
            }
        });
    }

    public void delete() {
        if (this.scheduledFuture != null && this.scheduledFuture.cancel(false)) {
            this.scheduledFuture = null;
            hook.deleteOriginal().queue(delete -> {
                for (EmbedListener listener : listeners) {
                    listener.onEmbedDelete();
                }
            });
        }
    }

    protected abstract EmbedLayout buildLayout();


}
