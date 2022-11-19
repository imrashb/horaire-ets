package me.imrashb.discord.events.action;

import lombok.Data;
import me.imrashb.discord.events.handler.InteractionHandler;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.Arrays;
import java.util.List;

@Data
public abstract class DeferredAction<ReturnAction extends DeferredAction> {

    private List<Class<? extends InteractionHandler>> supportedHandlers;

    public DeferredAction(Class<? extends InteractionHandler>... supportedInteractions) {
        this.supportedHandlers = Arrays.asList(supportedInteractions);
    }

    public final boolean isSupported(InteractionHandler handler) {
        return supportedHandlers.contains(handler.getClass());
    }

    public abstract void start(Interaction interaction);
    public abstract ReturnAction execute(Interaction interaction);
    public abstract void cleanup(Interaction interaction);

    public abstract boolean isProcessable(Interaction event);




}
