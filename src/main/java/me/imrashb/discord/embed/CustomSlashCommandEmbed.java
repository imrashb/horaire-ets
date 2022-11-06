package me.imrashb.discord.embed;

import lombok.Data;
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

    private List<FunctionalItemComponent> components = null;

    private ScheduledFuture<?> scheduledFuture = null;

    @Getter
    private InteractionHook hook;

    @Getter
    private Long messageId;

    private long alive = 60;


    @Getter @Setter
    private boolean stayAlive = false;

    public boolean getStayAlive() {
        return this.stayAlive;
    }

    public final void queueEmbed(SlashCommandInteractionEvent event, boolean ephemeral) {
        components = this.buildComponents();
        List<ItemComponent> itemComponents = new ArrayList<>();
        for(FunctionalItemComponent c : components) itemComponents.add(c.getComponent());

        event.replyEmbeds(this.update().build()).addActionRow(itemComponents).setEphemeral(ephemeral).timeout(alive, TimeUnit.SECONDS).queue((hook) -> {
            this.hook = hook;
            hook.retrieveOriginal().queue(message -> {
                this.messageId = message.getIdLong();
            });
            this.deleteAfterInactivity();
        });

    }

    protected abstract EmbedBuilder update();

    public void fireUpdate(GenericComponentInteractionCreateEvent event) {

        for(FunctionalItemComponent comp : this.components) {
            if(comp.getComponent().getId().equals(event.getComponentId())) {
                comp.consume(event);
                break;
            }
        }

        if(this.scheduledFuture != null && this.scheduledFuture.isCancelled() || !this.scheduledFuture.cancel(false)) {
            return;
        }

        hook
                .editOriginalEmbeds(this.update().build())
                .timeout(alive, TimeUnit.SECONDS)
                .queue();
        event.deferEdit().queue();
        this.deleteAfterInactivity();
    }

    private void deleteAfterInactivity() {
        if(!stayAlive)
            this.scheduledFuture = hook.deleteOriginal().queueAfter(alive, TimeUnit.SECONDS);
    }

    protected abstract List<FunctionalItemComponent> buildComponents();



}
