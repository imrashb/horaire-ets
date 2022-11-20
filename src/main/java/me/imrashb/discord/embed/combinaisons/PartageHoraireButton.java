package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.*;
import me.imrashb.discord.utils.*;
import me.imrashb.domain.*;
import me.imrashb.utils.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.*;

public class PartageHoraireButton extends StatefulActionComponent<Button> {

    private final AtomicInteger currentCombinaison;
    private final List<CombinaisonHoraire> combinaisons;
    private final String sessionId;
    private List<Message> messages = new ArrayList<>();

    public PartageHoraireButton(AtomicInteger currentCombinaison, List<CombinaisonHoraire> combinaisons, String sessionId, DomainUser user) {
        super( "partage",user);
        this.currentCombinaison = currentCombinaison;
        this.combinaisons = combinaisons;
        this.sessionId = sessionId;
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        for(Message m : messages) {
            m.delete().queue();
        }
        messages.clear();
        CombinaisonHoraire comb = this.combinaisons.get(currentCombinaison.get());
        Image img = new HoraireImageMaker(comb, getTheme(user)).drawHoraire();
        event.getMessageChannel().sendMessage(":newspaper: Horaire partagée par <@"+event.getUser().getIdLong()+"> :newspaper:")
                .setFiles(MessageUtils.getFileUploadFromImage(img, comb.getUniqueId()+".jpeg"))
                .mention(event.getUser()).queue(message -> {
                    messages.add(message);
                });
    }

    @Override
    public Button draw() {
        return Button.secondary("partage", "Partager ("+getTheme(user).getNom()+")")
                .withEmoji(Emoji.fromUnicode("\uD83D\uDCCE"));
    }

    private HoraireImageMakerTheme getTheme(DomainUser user) {
        Optional<HoraireImageMakerTheme> theme = HoraireImageMaker.themes.stream().filter(t -> t.getId().equals(user.getPreferences().getThemeId())).findFirst();
        return theme.orElse(HoraireImageMaker.LIGHT_THEME);
    }
}
