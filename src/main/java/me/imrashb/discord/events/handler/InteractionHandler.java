package me.imrashb.discord.events.handler;

import lombok.AccessLevel;
import lombok.Getter;
import me.imrashb.discord.events.action.DeferredAction;
import net.dv8tion.jda.api.interactions.Interaction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class InteractionHandler<I extends Interaction,A extends DeferredAction> {
    private Class<I> interactionType;
    @Getter
    private boolean initialHandler;
    @Getter(value = AccessLevel.PROTECTED)
    private List<DeferredAction> actions;

    public InteractionHandler(boolean initialHandler) {
        this.actions = new ArrayList<>();
        this.initialHandler = initialHandler;
        Type[] types = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments();
        this.interactionType = (Class<I>) types[0];
    }

    public final A process(I interaction, A action) {
        A deferredAction = this.processInteraction(interaction, action);
        this.actions.remove(action);
        return deferredAction;
    }

    public final boolean isMatchingInteractionType(Class<I> interactionType) {
        return this.interactionType.isAssignableFrom(interactionType);
    }

    public final void addDeferredAction(DeferredAction action) {
        this.actions.add(action);
    }

    protected abstract A processInteraction(I interaction, A action);

    public DeferredAction getProcessableDeferredAction(I interaction) {
        if(this.initialHandler) return null;

        for(DeferredAction action : this.getActions()) {
            if(action.isProcessable(interaction)) {
                return action;
            }
        }
        return null;
    }

}
