package me.imrashb.discord.events;

import lombok.*;
import me.imrashb.discord.button.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.hooks.*;

import java.util.*;

@AllArgsConstructor
public class ButtonInteractionEventHandler extends ListenerAdapter {

    @NonNull
    public static Set<ReactiveButton> buttons = new HashSet<>();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        final String commandId = event.getComponentId();
        for(ReactiveButton button : buttons) {
            if(button.isMatchingButton(commandId)) {
                button.execute(event);
                break;
            }
        }

    }

}
