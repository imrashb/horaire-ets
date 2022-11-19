package me.imrashb.discord.embed;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public abstract class StatefulActionComponent<Component extends ActionComponent> {

    @Getter @Setter
    private Component component;

    private Map<String, Object> states = new HashMap<>();

    public StatefulActionComponent(Component component) {
        this.component = component;
    }

    public abstract void execute(GenericComponentInteractionCreateEvent event);

    public abstract Component draw(Component component);

    public void setState(String key, Object value) {
        if(this.states.containsKey(key)) {
            this.states.replace(key, value);
        } else {
            this.states.put(key, value);
        }
    }

    public Object getState(String key) {
        return this.states.get(key);
    }

}
