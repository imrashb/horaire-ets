package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.BotConstants;
import me.imrashb.discord.embed.CustomSlashCommandEmbed;
import me.imrashb.discord.embed.EmbedLayout;
import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.discord.utils.*;
import me.imrashb.domain.*;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.CombinaisonHoraireComparator;
import me.imrashb.domain.combinaison.comparator.LostTimeComparator;
import me.imrashb.domain.combinaison.comparator.CongesComparator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

import static me.imrashb.discord.utils.MessageUtils.bold;
import static me.imrashb.discord.utils.MessageUtils.italic;

public class CombinaisonsEmbed extends CustomSlashCommandEmbed {
    private final List<CombinaisonHoraire> combinaisons;
    private final String sessionId;
    private final List<Message> messages = new ArrayList<>();
    private final AtomicInteger currentCombinaison = new AtomicInteger(0);
    private PreferencesUtilisateur preferences;

    public CombinaisonsEmbed(List<CombinaisonHoraire> combinaisons, String sessionId, DomainUser user) {
        super(user, true);
        this.combinaisons = combinaisons;
        this.sessionId = sessionId;

        CombinaisonHoraireComparator comparator = null;
        try {
            comparator = new CombinaisonHoraireComparator.Builder()
                    .addComparator(CongesComparator.class)
                    .addComparator(LostTimeComparator.class).build();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(combinaisons, comparator);
    }

    @Override
    public EmbedBuilder update() {
        embedBuilder.clear();
        CombinaisonHoraire comb = this.combinaisons.get(currentCombinaison.get());

        for (int i = 0; i < comb.getGroupes().size(); i++) {
            Groupe groupe = comb.getGroupes().get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(CombinaisonUtils.SYMBOLES_COURS[i] + "\n");
            for (Activite a : groupe.getActivites()) {
                if (a.getCharges().size() > 0) {
                    sb.append(bold(a.getNom()));
                    if (a.getLocaux().size() > 0) {
                        sb.append(" (").append(italic(String.join(", ", a.getLocaux()))).append(")");
                    }
                    sb.append("\n").append(String.join("\n", a.getCharges())).append("\n\n");
                }
            }

            embedBuilder.addField(groupe.toString(), sb.toString(), true);
        }

        embedBuilder.setTitle("Horaire " + (currentCombinaison.get() + 1));
        embedBuilder.setColor(BotConstants.EMBED_COLOR);
        embedBuilder.appendDescription(combinaisons.size() + " combinaisons trouvés");

        String conges = comb.getConges().stream().map(Jour::getNom).collect(Collectors.joining(", "));
        String stringCombinaison = CombinaisonUtils.getCombinaisonString(comb);
        embedBuilder.addField("Horaire", stringCombinaison, false);
        embedBuilder.addField("Congés", conges, true);

        return embedBuilder;
    }

    @Override
    protected EmbedLayout buildLayout() {
        StatefulActionComponent<Button> prochain = new ProchainCombinaisonButton(this.currentCombinaison, this.combinaisons);
        StatefulActionComponent<Button> precedent = new PrecedentCombinaisonButton(this.currentCombinaison, this.combinaisons);
        StatefulActionComponent<StringSelectMenu> choix = new SelectCombinaisonDropdown(this.currentCombinaison, this.combinaisons);
        StatefulActionComponent<Button> partage = new PartageHoraireButton(currentCombinaison, combinaisons, sessionId, getUser());
        StatefulActionComponent<StringSelectMenu> theme = new ThemeCombinaisonMenu(getUser());
        StatefulActionComponent<Button> monHoraire = new MonHoraireButton(currentCombinaison, combinaisons, sessionId, getUser());
        StatefulActionComponent<StringSelectMenu> filtre = new ComparateurHoraireDropdown(currentCombinaison, combinaisons);

        return new EmbedLayout().addActionRow(precedent, prochain).addActionRow(choix).addActionRow(filtre).addActionRow(monHoraire, partage).addActionRow(theme);
    }

}
