package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.domain.CombinaisonHoraire;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectCombinaisonDropdown extends StatefulActionComponent<StringSelectMenu> {

    private AtomicInteger currentCombinaison;
    private List<CombinaisonHoraire> combinaisons;

    public SelectCombinaisonDropdown(AtomicInteger currentCombinaison, List<CombinaisonHoraire> combinaisons) {
        super(null);
        this.currentCombinaison = currentCombinaison;
        this.combinaisons = combinaisons;
        StringSelectMenu menu = draw(null);
        setComponent(menu);
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        StringSelectInteractionEvent e = (StringSelectInteractionEvent) event;
        String value = e.getValues().get(0);
        int start = Math.max(0, currentCombinaison.get()-12);
        int end = Math.min(currentCombinaison.get()+12, combinaisons.size()-1);
        if(value.equals(ID_HORAIRES_PRECEDENT)) {
            currentCombinaison.set(Math.max(0, end-24));
        } else if(value.equals(ID_HORAIRES_SUIVANT)) {
            currentCombinaison.set(Math.min(start+24, combinaisons.size()-1));
        } else {
            int index = Integer.parseInt(value);
            currentCombinaison.set(index);
        }
    }

    @Override
    public StringSelectMenu draw(StringSelectMenu component) {
        return getCombinaisonSelectMenu();
    }

    private static final String ID_HORAIRES_PRECEDENT = "horaires_precedents";
    private static final String ID_HORAIRES_SUIVANT = "horaires_suivants";

    public StringSelectMenu getCombinaisonSelectMenu() {
        StringSelectMenu.Builder menu = StringSelectMenu.create("choix");

        List<SelectOption> options = new ArrayList<>();

        int lowerBound = Math.min(combinaisons.size() - currentCombinaison.get(), 12);

        int start = this.currentCombinaison.get() - (25 - lowerBound);
        if (start < 0) start = 0;
        if (start > 0) {
            SelectOption opt = SelectOption.of("Voir les horaires précédents", ID_HORAIRES_PRECEDENT);
            options.add(opt);
            start++;
        }

        for (int i = start; i < combinaisons.size(); i++) {
            if (options.size() == 24 && i != combinaisons.size() - 1) {
                SelectOption opt = SelectOption.of("Voir les horaires suivants", ID_HORAIRES_SUIVANT);
                options.add(opt);
                break;
            }

            CombinaisonHoraire c = combinaisons.get(i);
            StringBuilder sb = new StringBuilder();
            c.getConges().forEach(s -> sb.append(s.getNom() + ", "));

            SelectOption opt = SelectOption.of("Horaire " + (i + 1), i + "").withDescription("Congés: " + sb.toString());
            if (i == currentCombinaison.get()) opt = opt.withDefault(true);
            options.add(opt);
        }
        menu.addOptions(options);
        return menu.build();
    }

}
