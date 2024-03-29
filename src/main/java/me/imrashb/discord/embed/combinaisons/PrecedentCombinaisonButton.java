package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PrecedentCombinaisonButton extends StatefulActionComponent<Button> {

    private final AtomicInteger currentCombinaison;
    private final List<CombinaisonHoraire> combinaisons;

    public PrecedentCombinaisonButton(AtomicInteger currentCombinaison, List<CombinaisonHoraire> combinaisons) {
        super("precedent", null);
        this.currentCombinaison = currentCombinaison;
        this.combinaisons = combinaisons;
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        this.currentCombinaison.decrementAndGet();
        if (currentCombinaison.get() >= combinaisons.size()) {
            currentCombinaison.set(0);
        } else if (currentCombinaison.get() < 0) {
            currentCombinaison.set(combinaisons.size() - 1);
        }
    }

    @Override
    public Button draw() {
        return Button.primary(getId(), "Précédent")
                .withEmoji(Emoji.fromUnicode("⬅"));
    }
}
