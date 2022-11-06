package me.imrashb.discord.embed;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

import java.util.function.Consumer;

public class FunctionalItemComponent {

    private Consumer<GenericComponentInteractionCreateEvent> consumer;

    @Getter
    private ActionComponent component;

    public FunctionalItemComponent(ActionComponent component, Consumer<GenericComponentInteractionCreateEvent> consumer) {
        this.consumer = consumer;
        this.component = component;
    }

    public void consume(GenericComponentInteractionCreateEvent event) {
        consumer.accept(event);
    }

}
