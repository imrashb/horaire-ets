package me.imrashb.discord.embed;

import lombok.Getter;
import me.imrashb.discord.utils.DomainUser;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbedLayout {

    @Getter
    private final List<ActionRow> rows;
    private final List<StatefulActionComponent> components;

    public EmbedLayout() {
        this.rows = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    public EmbedLayout addActionRow(StatefulActionComponent... components) {
        List<ItemComponent> liste = new ArrayList<>();
        for (StatefulActionComponent comp : components) {
            if (comp.getComponent() == null) {
                ActionComponent c = comp.draw();
                comp.setComponent(c);
            }
            liste.add(comp.getComponent());
        }
        if (liste.size() == 0) return this;
        rows.add(ActionRow.of(liste));
        this.components.addAll(Arrays.asList(components));
        return this;
    }

    public void update(GenericComponentInteractionCreateEvent event, DomainUser user) {
        for (StatefulActionComponent c : components) {
            // Execute event
            if (c.getId().equals(event.getComponentId())) {
                c.execute(event);
                break;
            }
        }

        //Redraw components
        for (StatefulActionComponent c : components) {
            ActionComponent comp = c.draw();
            c.setComponent(comp);
            if (comp == null) continue;
            for (ActionRow a : rows) {
                a.updateComponent(comp.getId(), comp);
            }
        }
    }


}
