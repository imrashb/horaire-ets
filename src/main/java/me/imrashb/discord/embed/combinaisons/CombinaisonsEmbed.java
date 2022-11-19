package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.BotConstants;
import me.imrashb.discord.embed.CustomSlashCommandEmbed;
import me.imrashb.discord.embed.EmbedLayout;
import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.discord.utils.MessageUtils;
import me.imrashb.domain.*;
import me.imrashb.service.PreferencesUtilisateurService;
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
    private String sessionId;
    private PreferencesUtilisateurService preferencesUtilisateurService;
    private List<Message> messages = new ArrayList<>();
    private AtomicInteger currentCombinaison = new AtomicInteger(0);
    private PreferencesUtilisateur preferences;
    private User initialUser;
    private HoraireImageMakerTheme theme = HoraireImageMaker.LIGHT_THEME;

    public CombinaisonsEmbed(List<CombinaisonHoraire> combinaisons, User initialUser, String sessionId, PreferencesUtilisateurService preferencesUtilisateurService) {
        this.combinaisons = combinaisons;
        this.sessionId = sessionId;
        this.preferencesUtilisateurService = preferencesUtilisateurService;
        this.initialUser = initialUser;
        this.preferences = preferencesUtilisateurService.getPreferencesUtilisateur(initialUser.getIdLong());

        this.theme = HoraireImageMaker.getThemeFromId(preferences.getThemeId());
    }

    @Override
    public EmbedBuilder update() {
        embedBuilder.clear();
        CombinaisonHoraire comb = this.combinaisons.get(currentCombinaison.get());

        for(int i = 0; i<comb.getGroupes().size(); i++) {
            Groupe groupe = comb.getGroupes().get(i);
            embedBuilder.addField(groupe.toString(), CombinaisonUtils.SYMBOLES_COURS[i], true);
        }

        embedBuilder.setTitle("Horaire "+(currentCombinaison.get()+1));
        embedBuilder.setColor(BotConstants.EMBED_COLOR);
        embedBuilder.appendDescription(combinaisons.size()+" combinaisons trouvés");
        embedBuilder.addField("Identifiant unique de l'horaire", comb.getUniqueId(), false);
        String stringCombinaison = CombinaisonUtils.getCombinaisonString(comb);
        embedBuilder.addField("Horaire", stringCombinaison, false);

        StringBuilder conges = new StringBuilder();
        comb.getConges().forEach(conge -> conges.append(conge.getNom()+", "));

        embedBuilder.addField("Congés", conges.toString(), true);

        return embedBuilder;
    }

    @Override
    protected EmbedLayout buildLayout() {
        StatefulActionComponent<Button> prochain = new ProchainCombinaisonButton(this.currentCombinaison, this.combinaisons);
        StatefulActionComponent<Button> precedent = new PrecedentCombinaisonButton(this.currentCombinaison, this.combinaisons);
        StatefulActionComponent<StringSelectMenu> choix = new SelectCombinaisonDropdown(this.currentCombinaison, this.combinaisons);


        StatefulActionComponent partage = new StatefulActionComponent<Button>(
                Button.secondary("partage", "Partager ("+this.theme.getNom()+")")
                        .withEmoji(Emoji.fromUnicode("\uD83D\uDCCE"))) {
            @Override
            public void execute(GenericComponentInteractionCreateEvent event) {
                partagerHoraire(event, CombinaisonsEmbed.this.theme);
            }

            @Override
            public Button draw(Button component) {
                return component.withLabel("Partager ("+CombinaisonsEmbed.this.theme.getNom()+")");
            }
        };

        StatefulActionComponent theme = new StatefulActionComponent<StringSelectMenu>(getSelectTheme()) {
            @Override
            public void execute(GenericComponentInteractionCreateEvent event) {
                StringSelectInteractionEvent e = (StringSelectInteractionEvent) event;
                CombinaisonsEmbed.this.theme = HoraireImageMaker.themes.get(Integer.parseInt(e.getValues().get(0)));
                CombinaisonsEmbed.this.preferences.setThemeId(CombinaisonsEmbed.this.theme.getId());
                CombinaisonsEmbed.this.preferences = preferencesUtilisateurService.savePreferencesUtilisateur(CombinaisonsEmbed.this.preferences);
            }

            @Override
            public StringSelectMenu draw(StringSelectMenu component) {
                return getSelectTheme();
            }

        };

        StatefulActionComponent monHoraire = new StatefulActionComponent<Button>(getPreferenceButton(null)) {
            @Override
            public void execute(GenericComponentInteractionCreateEvent event) {

                CombinaisonHoraire comb = combinaisons.get(currentCombinaison.get());

                if(preferences.getHoraires().containsKey(sessionId)) {
                    preferences.getHoraires().replace(sessionId, comb.getUniqueId());
                } else {
                    preferences.getHoraires().put(sessionId, comb.getUniqueId());
                }

                preferencesUtilisateurService.savePreferencesUtilisateur(preferences);
            }

            @Override
            public Button draw(Button component) {
                return getPreferenceButton(component);
            }
        };

        return new EmbedLayout().addActionRow(precedent, prochain).addActionRow(choix).addActionRow(monHoraire, partage).addActionRow(theme);
    }

    private Button getPreferenceButton(Button button) {
        CombinaisonHoraire comb = combinaisons.get(currentCombinaison.get());
        Button newButton = button == null ? Button.secondary("monhoraire", Emoji.fromUnicode("❤")) : button;

        if(this.preferences != null && comb.getUniqueId().equals(this.preferences.getHoraires().get(sessionId))) {
            newButton = newButton.withLabel("Déjà mon horaire pour "+this.sessionId).withDisabled(true);
        } else {
            newButton = newButton.withLabel("Rendre cela mon horaire pour "+this.sessionId).withDisabled(false);
        }

        return newButton;
    }

    private StringSelectMenu getSelectTheme() {
        StringSelectMenu.Builder menu = StringSelectMenu.create("theme");
        List<SelectOption> options = new ArrayList<>();
        int i = 0;
        for(HoraireImageMakerTheme t : HoraireImageMaker.themes) {
            SelectOption opt = SelectOption.of(t.getNom(), i+"");
            if(t == this.theme) opt = opt.withDefault(true);
            options.add(opt);
            i++;
        }

        menu.addOptions(options);

        return menu.build();
    }

    private void partagerHoraire(GenericComponentInteractionCreateEvent event, HoraireImageMakerTheme theme) {
        for(Message m : messages) {
            m.delete().queue();
        }
        messages.clear();
        CombinaisonHoraire comb = this.combinaisons.get(currentCombinaison.get());
        Image img = new HoraireImageMaker(comb, this.theme).drawHoraire();
        final String username =  event.getUser().getName();
        event
                .reply(":newspaper: Horaire partagée par <@"+event.getUser().getIdLong()+"> :newspaper:")
                .setFiles(MessageUtils.getFileUploadFromImage(img, comb.getUniqueId()))
                .mention(event.getUser()).queue(message -> {
                    event.reply("Voici l'horaire de "+username+" pour la session '"+sessionId+"'.")
                            .setEphemeral(true).queue();
                });
    }

}
