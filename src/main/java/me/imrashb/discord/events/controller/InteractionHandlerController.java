package me.imrashb.discord.events.controller;

import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.events.handler.InteractionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.ArrayList;
import java.util.List;


public class InteractionHandlerController extends ListenerAdapter {

    private JDA jda;
    private List<InteractionHandler> handlers;

    public InteractionHandlerController(JDA jda) {
        this.jda = jda;
        this.handlers = new ArrayList<>();
    }

    public void addInteractionHandler(InteractionHandler eventHandler) {
        this.handlers.add(eventHandler);
    }

    public void onGenericEvent(GenericEvent event) {

        if(!(event instanceof Interaction)) return;

        Interaction interaction = (Interaction) event;

        boolean processed = false;
        for(InteractionHandler handler : handlers) {
            if(handler.isMatchingInteractionType(interaction.getClass())) {
                DeferredAction action = null;

                if(handler.isInitialHandler()) {
                    action = handler.process(interaction, null);
                    processed = true;
                } else {
                    DeferredAction processableAction = handler.getProcessableDeferredAction(interaction);

                    if(processableAction == null) {
                        continue;
                    } else {
                        action = handler.process(interaction, processableAction);
                        processed = true;
                    }
                }

                if(action == null) continue;

                // Add deferrable action to handlers that can process it
                for(InteractionHandler h : handlers) {
                    if(action.isSupported(h)) {
                        h.addDeferredAction(action);
                    }
                }

            }
        }

        if(!processed) {
            // TODO UNPROCESSED ACTION
        }

    }


}
