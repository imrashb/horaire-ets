package me.imrashb.discord.events.action;

import lombok.Data;
import me.imrashb.discord.events.handler.InteractionHandler;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class DeferredAction<ReturnAction extends DeferredAction> {

    private List<Class<? extends InteractionHandler>> supportedHandlers;
    private List<DeferredActionListener> listeners;

    @SafeVarargs
    public DeferredAction(Class<? extends InteractionHandler>... supportedInteractions) {
        this.supportedHandlers = Arrays.asList(supportedInteractions);
        this.listeners = new ArrayList<>();
    }

    public final void addDeferredActionListener(DeferredActionListener listener) {
        listeners.add(listener);
    }

    public final boolean isSupported(InteractionHandler handler) {
        return supportedHandlers.contains(handler.getClass());
    }

    public abstract ReturnAction execute(Interaction interaction);

    public final void cleanup() {
        for (DeferredActionListener listener : listeners) {
            listener.onCleanup();
        }
    }

    public abstract boolean isProcessable(Interaction event);


}
