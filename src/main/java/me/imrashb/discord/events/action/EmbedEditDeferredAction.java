package me.imrashb.discord.events.action;

import me.imrashb.discord.embed.CustomSlashCommandEmbed;
import me.imrashb.discord.events.handler.ComponentControlledEmbedHandler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.List;

public class EmbedEditDeferredAction extends DeferredAction<EmbedEditDeferredAction> {

    private List<User> authorizedUsers;
    private CustomSlashCommandEmbed embed;

    public EmbedEditDeferredAction(List<User> authorizedUsers, CustomSlashCommandEmbed embed) {
        super(ComponentControlledEmbedHandler.class);
        this.authorizedUsers = authorizedUsers;
        this.embed = embed;
    }

    @Override
    public void start(Interaction interaction) {

    }

    @Override
    public EmbedEditDeferredAction execute(Interaction interaction) {

        if(!(interaction instanceof GenericComponentInteractionCreateEvent)) throw new RuntimeException("IMPOSSIBLE WTF");

        this.embed.fireUpdate((GenericComponentInteractionCreateEvent) interaction);

        return this;
    }

    @Override
    public void cleanup(Interaction interaction) {
        embed.getHook().deleteOriginal().queue();
    }

    @Override
    public boolean isProcessable(Interaction event) {
        GenericComponentInteractionCreateEvent interaction = (GenericComponentInteractionCreateEvent) event;
        return this.authorizedUsers.contains(event.getUser())
                && embed.getMessageId() == interaction.getMessageIdLong();
    }
}
