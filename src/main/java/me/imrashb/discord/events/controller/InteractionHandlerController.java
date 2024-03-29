package me.imrashb.discord.events.controller;

import me.imrashb.discord.events.action.DeferredAction;
import me.imrashb.discord.events.handler.InteractionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.exceptions.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.ArrayList;
import java.util.List;


public class InteractionHandlerController extends ListenerAdapter {

    private final List<InteractionHandler> handlers;
    private User owner;

    public InteractionHandlerController(JDA jda) {
        this.handlers = new ArrayList<>();
        String ownerId = "231139969089929218";
        jda.retrieveUserById(ownerId).queue(user -> {
            owner = user;
        });
    }

    public void addInteractionHandler(InteractionHandler eventHandler) {
        this.handlers.add(eventHandler);
    }

    public void onGenericEvent(GenericEvent event) {

        try {
            if (!(event instanceof Interaction)) return;

            Interaction interaction = (Interaction) event;

            boolean processed = false;
            for (InteractionHandler handler : handlers) {
                if (handler.isMatchingInteractionType(interaction.getClass())) {
                    DeferredAction action = null;

                    if (handler.isInitialHandler()) {
                        action = handler.process(interaction, null);
                        processed = true;
                    } else {
                        DeferredAction processableAction = handler.getProcessableDeferredAction(interaction);

                        if (processableAction == null) {
                            continue;
                        } else {
                            action = handler.process(interaction, processableAction);
                            processed = true;
                        }
                    }

                    if (action == null) continue;

                    // Add deferrable action to handlers that can process it
                    for (InteractionHandler h : handlers) {
                        if (action.isSupported(h)) {
                            h.addDeferredAction(action);
                        }
                    }

                }
            }

            if (!processed) {
                // TODO UNPROCESSED ACTION
            }
        } catch (Exception ex) {
            handleDiscordError(ex, event);
        }

    }

    private void handleDiscordError(Exception ex, GenericEvent event) {
        Interaction interaction = (Interaction) event;
        if (event instanceof IReplyCallback) {
            IReplyCallback cb = (IReplyCallback) event;
            if(ex instanceof InsufficientPermissionException) {
                InsufficientPermissionException e = (InsufficientPermissionException) ex;
                cb
                        .reply("Je n'ai pas la permission pour faire cela. Permission manquante: " + e.getPermission().getName())
                        .setEphemeral(true)
                        .queue();
            } else {
                cb.reply("Il y a eu une erreur inattendue. Elle a été signalée. Désolé de cet inconvénient.").setEphemeral(true).queue();

                ex.printStackTrace();

                try {
                    owner.openPrivateChannel().queue((channel) -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Erreur lors d'un évènement").append("\n");
                        if (interaction.getUser() != null) {
                            User user = interaction.getUser();
                            sb.append("Utilisateur: ").append(user.getIdLong()).append("/").append(user.getName()).append("#").append(user.getDiscriminator()).append("\n");
                        }
                        if (interaction.getType() != null)
                            sb.append("Type: ").append(interaction.getType().name()).append("\n");
                        if (interaction.getGuild() != null)
                            sb.append("Guild: ").append(interaction.getGuild().getIdLong()).append("/").append(interaction.getGuild().getName()).append("\n");
                        sb.append("Message de l'exception: ").append(ex.getMessage());
                        channel.sendMessage(sb.toString()).queue();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
