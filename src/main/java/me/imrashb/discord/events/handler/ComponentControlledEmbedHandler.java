package me.imrashb.discord.events.handler;

import me.imrashb.discord.events.action.EmbedEditDeferredAction;
import net.dv8tion.jda.api.events.interaction.component.*;


public class ComponentControlledEmbedHandler extends InteractionHandler<GenericComponentInteractionCreateEvent, EmbedEditDeferredAction> {
    public ComponentControlledEmbedHandler() {
        super(false);
    }

    @Override
    public EmbedEditDeferredAction processInteraction(GenericComponentInteractionCreateEvent interaction, EmbedEditDeferredAction action) {
        return action.execute(interaction);
    }

}
