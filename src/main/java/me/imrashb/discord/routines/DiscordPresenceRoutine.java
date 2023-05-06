package me.imrashb.discord.routines;

import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DiscordPresenceRoutine extends PeriodicRoutine {

    private final JDA jda;
    private final List<Supplier<Activity>> suppliers;
    private int currentSupplier = 0;

    public DiscordPresenceRoutine(@NonNull final JDA jda) {
        super(Duration.ofSeconds(60));
        this.jda = jda;
        this.suppliers = new ArrayList<>();
        this.initSuppliers();
    }

    private Activity getListeningHorairETS() {
        return Activity.listening("/horairets");
    }

    private Activity getWatchingGuildAmount() {
        int amount = jda.getGuilds().size();
        return Activity.watching(amount + " serveurs");
    }

    private static final String HORAIRETS_LINK = "https://horairets.emmanuelcoulombe.dev";
    private Activity getWatchingHorairETSWebsite() {
        return Activity.watching(HORAIRETS_LINK);
    }

    private void initSuppliers() {
        suppliers.add(this::getWatchingGuildAmount);
        suppliers.add(this::getListeningHorairETS);
        suppliers.add(this::getWatchingHorairETSWebsite);
    }

    @Override
    public void run() {
        if (currentSupplier >= suppliers.size()) {
            currentSupplier = 0;
        }

        jda.getPresence().setPresence(suppliers.get(currentSupplier).get(), true);
        currentSupplier++;
    }
}
