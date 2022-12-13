package me.imrashb.discord.routines;

import lombok.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;

import java.time.*;
import java.util.*;
import java.util.function.*;

public class DiscordPresenceRoutine extends PeriodicRoutine{

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
        return Activity.watching(amount+" serveurs");
    }

    private void initSuppliers() {
        suppliers.add(this::getWatchingGuildAmount);
        suppliers.add(this::getListeningHorairETS);
    }

    @Override
    public void run() {
        if(currentSupplier >= suppliers.size()) {
            currentSupplier = 0;
        }

        jda.getPresence().setPresence(suppliers.get(currentSupplier).get(), true);
        currentSupplier++;
    }
}
