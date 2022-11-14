package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.CustomSlashCommandEmbed;
import me.imrashb.discord.embed.EmbedLayout;
import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.domain.*;
import me.imrashb.utils.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CombinaisonsEmbed extends CustomSlashCommandEmbed {
    private List<CombinaisonHoraire> combinaisons;
    private List<Message> messages = new ArrayList<>();
    private AtomicInteger currentCombinaison = new AtomicInteger(0);

    public CombinaisonsEmbed(List<CombinaisonHoraire> combinaisons) {
        this.combinaisons = combinaisons;
    }

    @Override
    public EmbedBuilder update() {

        if(currentCombinaison.get() >= combinaisons.size()) {
            currentCombinaison.set(0);
        } else if(currentCombinaison.get() < 0 ) {
            currentCombinaison.set(combinaisons.size()-1);
        }

        embedBuilder.clear();

        CombinaisonHoraire comb = this.combinaisons.get(currentCombinaison.get());

        for(int i = 0; i<comb.getGroupes().size(); i++) {
            Groupe groupe = comb.getGroupes().get(i);
            embedBuilder.addField(groupe.toString(), CombinaisonUtils.SYMBOLES_COURS[i], true);
        }

        embedBuilder.setTitle("Horaire "+(currentCombinaison.get()+1));
        embedBuilder.appendDescription(combinaisons.size()+" combinaisons trouvés");
        String stringCombinaison = CombinaisonUtils.getCombinaisonString(comb);
        embedBuilder.addField("Horaire", stringCombinaison, false);

        StringBuilder conges = new StringBuilder();
        comb.getConges().forEach(conge -> conges.append(conge.getNom()+", "));

        embedBuilder.addField("Congés", conges.toString(), true);

        return embedBuilder;
    }

    @Override
    protected EmbedLayout buildLayout() {
        StatefulActionComponent<Button> prochain = new ProchainCombinaisonButton(this.currentCombinaison);
        StatefulActionComponent<Button> precedent = new PrecedentCombinaisonButton(this.currentCombinaison);
        StatefulActionComponent<StringSelectMenu> choix = new SelectCombinaisonDropdown(this.currentCombinaison, this.combinaisons);


        StatefulActionComponent<Button> epingle = new StatefulActionComponent<Button>(
                Button.secondary("epingle", "Épingler")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCC"))) {
            @Override
            public void execute(GenericComponentInteractionCreateEvent event) {
                setStayAlive(!getStayAlive());
            }

            @Override
            public Button draw(Button component) {
                return component.withLabel(getStayAlive() ? "Oublier" : "Épingler");
            }
        };
        StatefulActionComponent partage = new StatefulActionComponent<Button>(
                Button.secondary("partage", "Partager")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCE"))) {
            @Override
            public void execute(GenericComponentInteractionCreateEvent event) {

                for(Message m : messages) {
                    m.delete().queue();
                }
                messages.clear();

                Image image = new HoraireImageMaker(combinaisons.get(currentCombinaison.get())).drawHoraire();

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    ImageIO.write((RenderedImage) image, "jpeg", os);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                event.getMessageChannel()
                        .sendMessage(":newspaper: Horaire partagée par <@"+event.getUser().getIdLong()+"> :newspaper:")
                        .setFiles(FileUpload.fromData(os.toByteArray(), "Horaire "+(currentCombinaison.get()+1)+".jpeg"))
                        .mention(event.getUser()).queue(message -> {
                    messages.add(message);
                });
            }

            @Override
            public Button draw(Button component) {
                return component;
            }
        };
        return new EmbedLayout().addActionRow(precedent, prochain).addActionRow(choix).addActionRow(epingle, partage);
    }

}
