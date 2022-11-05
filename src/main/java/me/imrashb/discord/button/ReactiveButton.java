package me.imrashb.discord.button;

import lombok.*;
import me.imrashb.discord.events.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.interactions.components.*;

import java.util.*;
import java.util.function.*;

@Data
public abstract class ReactiveButton {

    private String id = UUID.randomUUID().getMostSignificantBits()+"";

    private ItemComponent component;

    public ReactiveButton() {
        this.component = getItemComponent(id);
    }

    public boolean isMatchingButton(String id) {
        return this.id.equals(id);
    }

    public abstract ItemComponent getItemComponent(String id);

    public abstract void execute(ButtonInteractionEvent event);

}
