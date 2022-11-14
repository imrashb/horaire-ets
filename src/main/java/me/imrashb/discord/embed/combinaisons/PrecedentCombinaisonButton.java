package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.StatefulActionComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.concurrent.atomic.AtomicInteger;

public class PrecedentCombinaisonButton extends StatefulActionComponent<Button> {

    private AtomicInteger currentCombinaison;

    public PrecedentCombinaisonButton(AtomicInteger currentCombinaison) {
        super(Button.primary("precedent", "Précédent")
                .withEmoji(Emoji.fromUnicode("⬅")));
        this.currentCombinaison = currentCombinaison;
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        this.currentCombinaison.decrementAndGet();
    }

    @Override
    public Button draw(Button component) {
        return component;
    }
}
