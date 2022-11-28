package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.CombinaisonHoraireComparator;
import me.imrashb.domain.combinaison.comparator.LostTimeComparator;
import me.imrashb.domain.combinaison.comparator.NombreJoursAvecCoursComparator;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ComparateurHoraireDropdown extends StatefulActionComponent<StringSelectMenu> {

    private final List<CombinaisonHoraireComparator> COMPARATEURS;
    private final AtomicInteger currentCombinaison;
    private final List<CombinaisonHoraire> combinaisons;
    private CombinaisonHoraireComparator selectedComparateur = null;

    public ComparateurHoraireDropdown(AtomicInteger currentCombinaison, List<CombinaisonHoraire> combinaisons) {
        super("comparateur", null);
        this.currentCombinaison = currentCombinaison;
        this.combinaisons = combinaisons;
        COMPARATEURS = new ArrayList<>();
        COMPARATEURS.add(new LostTimeComparator(new NombreJoursAvecCoursComparator(null)));
        COMPARATEURS.add(new LostTimeComparator(null));
        COMPARATEURS.add(new NombreJoursAvecCoursComparator(null));
        this.selectedComparateur = COMPARATEURS.get(0);
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        StringSelectInteractionEvent e = (StringSelectInteractionEvent) event;
        CombinaisonHoraireComparator comp = COMPARATEURS.stream().filter(c -> c.getId().equals(e.getValues().get(0))).findFirst().get();
        this.selectedComparateur = comp;
        this.currentCombinaison.set(0);
        Collections.sort(combinaisons, comp);
    }

    @Override
    public StringSelectMenu draw() {
        StringSelectMenu.Builder menu = StringSelectMenu.create(getId());
        for (CombinaisonHoraireComparator comp : COMPARATEURS) {
            SelectOption opt = SelectOption.of(comp.getDescription(), comp.getId());
            if (this.selectedComparateur == comp) opt = opt.withDefault(true);
            menu.addOptions(opt);
        }
        menu.setPlaceholder("Filtrer les combinaisons d'horaires");
        return menu.build();
    }
}
