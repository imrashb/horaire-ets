package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.*;
import me.imrashb.discord.utils.*;
import me.imrashb.domain.*;
import net.dv8tion.jda.api.entities.emoji.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.interactions.components.buttons.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public class MonHoraireButton extends StatefulActionComponent<Button> {

    private final String sessionId;
    private final AtomicInteger currentCombinaison;
    private final List<CombinaisonHoraire> combinaisons;

    public MonHoraireButton(AtomicInteger currentCombinaison, List<CombinaisonHoraire> combinaisons, String sessionId, DomainUser user) {
        super( "monhoraire",user);
        this.currentCombinaison = currentCombinaison;
        this.combinaisons = combinaisons;
        this.sessionId = sessionId;
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        CombinaisonHoraire comb = combinaisons.get(currentCombinaison.get());

        if(user.getPreferences().getHoraires().containsKey(sessionId)) {
            user.getPreferences().getHoraires().replace(sessionId, comb.getUniqueId());
        } else {
            user.getPreferences().getHoraires().put(sessionId, comb.getUniqueId());
        }

        user.savePreferences();
    }

    @Override
    public Button draw() {
        CombinaisonHoraire comb = combinaisons.get(currentCombinaison.get());
        Button button = Button.secondary(getId(), Emoji.fromUnicode("❤"));

        if(user.getPreferences() != null && comb.getUniqueId().equals(user.getPreferences().getHoraires().get(sessionId))) {
            button = button.withLabel("Déjà mon horaire pour "+this.sessionId).withDisabled(true);
        } else {
            button = button.withLabel("Rendre cela mon horaire pour "+this.sessionId).withDisabled(false);
        }
        return button;
    }
}