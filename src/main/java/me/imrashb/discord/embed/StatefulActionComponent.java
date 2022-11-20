package me.imrashb.discord.embed;

import lombok.Getter;
import lombok.Setter;
import me.imrashb.discord.utils.*;
import me.imrashb.service.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public abstract class StatefulActionComponent<Component extends ActionComponent> {

    protected final DomainUser user;
    private Map<String, Object> states = new HashMap<>();
    @Getter @Setter
    private Component component;

    @Getter
    private String id;

    public StatefulActionComponent(String id, DomainUser user) {
        this.user = user;
        this.id = id;
    }

    public abstract void execute(GenericComponentInteractionCreateEvent event);

    public abstract Component draw();


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
