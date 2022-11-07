package me.imrashb.discord.embed;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbedLayout {

    @Getter
    private List<ActionRow> rows;
    private List<StatefulActionComponent> components;

    public EmbedLayout() {
        this.rows = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    public EmbedLayout addActionRow(StatefulActionComponent... components) {
        List<ItemComponent> liste = new ArrayList<>();
        for(StatefulActionComponent comp : components) {
            liste.add(comp.getComponent());
        }
        rows.add(ActionRow.of(liste));
        this.components.addAll(Arrays.asList(components));
        return this;
    }

    public void update(GenericComponentInteractionCreateEvent event) {
        for(StatefulActionComponent c : components) {
            if(c.getComponent().getId().equals(event.getComponentId())) {
                ActionComponent comp = c.execute(event, c.getComponent());
                c.setComponent(comp);
                for(ActionRow a : rows) {
                    a.updateComponent(c.getComponent().getId(), c.getComponent());
                }
            }
        }
    }


}
