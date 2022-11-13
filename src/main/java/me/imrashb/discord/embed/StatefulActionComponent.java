package me.imrashb.discord.embed;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

import java.util.function.*;

public abstract class StatefulActionComponent<Component extends ActionComponent> {

    @Getter @Setter
    private Component component;

    public StatefulActionComponent(Component component) {
        this.component = component;
    }

    public abstract Component execute(GenericComponentInteractionCreateEvent event, Component component);

}
