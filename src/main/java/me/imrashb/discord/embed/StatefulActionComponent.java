package me.imrashb.discord.embed;

import lombok.Getter;
import lombok.Setter;
import me.imrashb.discord.utils.DomainUser;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

import java.util.HashMap;
import java.util.Map;

public abstract class StatefulActionComponent<Component extends ActionComponent> {

    protected final DomainUser user;
    private final Map<String, Object> states = new HashMap<>();
    @Getter
    private final String id;
    @Getter
    @Setter
    private Component component;

    public StatefulActionComponent(String id, DomainUser user) {
        this.user = user;
        this.id = id;
    }

    public abstract void execute(GenericComponentInteractionCreateEvent event);

    public abstract Component draw();


    public void setState(String key, Object value) {
        this.states.put(key, value);
    }

    public Object getState(String key) {
        return this.states.get(key);
    }

}
