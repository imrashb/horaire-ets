package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.BotConstants;
import me.imrashb.discord.embed.CustomSlashCommandEmbed;
import me.imrashb.discord.embed.EmbedLayout;
import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.discord.utils.*;
import me.imrashb.domain.*;
import me.imrashb.service.*;
import me.imrashb.utils.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CombinaisonsEmbed extends CustomSlashCommandEmbed {
    private List<CombinaisonHoraire> combinaisons;
    private String sessionId;
    private List<Message> messages = new ArrayList<>();
    private AtomicInteger currentCombinaison = new AtomicInteger(0);
    private PreferencesUtilisateur preferences;
    public CombinaisonsEmbed(List<CombinaisonHoraire> combinaisons, String sessionId, DomainUser user) {
        super(user, true);
        this.combinaisons = combinaisons;
        this.sessionId = sessionId;
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
        StatefulActionComponent partage = new PartageHoraireButton(currentCombinaison, combinaisons, sessionId, getUser());
        StatefulActionComponent theme = new ThemeCombinaisonMenu(getUser());
        StatefulActionComponent monHoraire = new MonHoraireButton(currentCombinaison, combinaisons, sessionId, getUser());


        return new EmbedLayout().addActionRow(precedent, prochain).addActionRow(choix).addActionRow(monHoraire, partage).addActionRow(theme);
    }

}
