package me.imrashb.discord.embed.combinaisons;

import me.imrashb.discord.embed.StatefulActionComponent;
import me.imrashb.discord.utils.DomainUser;
import me.imrashb.utils.HoraireImageMaker;
import me.imrashb.utils.HoraireImageMakerTheme;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;

public class ThemeCombinaisonMenu extends StatefulActionComponent<StringSelectMenu> {


    public ThemeCombinaisonMenu(DomainUser user) {
        super("theme", user);
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent event) {
        StringSelectInteractionEvent e = (StringSelectInteractionEvent) event;
        HoraireImageMakerTheme theme = HoraireImageMaker.themes.get(Integer.parseInt(e.getValues().get(0)));
        user.getPreferences().setThemeId(theme.getId());
        user.savePreferences();
    }

    @Override
    public StringSelectMenu draw() {
        StringSelectMenu.Builder menu = StringSelectMenu.create(getId());
        List<SelectOption> options = new ArrayList<>();
        int i = 0;
        for (HoraireImageMakerTheme t : HoraireImageMaker.themes) {
            SelectOption opt = SelectOption.of(t.getNom(), i + "");
            if (t.getId().equals(user.getPreferences().getThemeId())) opt = opt.withDefault(true);
            options.add(opt);
            i++;
        }

        menu.addOptions(options);

        return menu.build();
    }
}
