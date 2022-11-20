package me.imrashb.discord.utils;

import lombok.*;
import me.imrashb.domain.*;
import me.imrashb.service.*;
import net.dv8tion.jda.api.entities.*;

public class DomainUser {

    @Getter
    private final User user;
    @Getter
    private final PreferencesUtilisateur preferences;
    private final HorairETSService service;

    public DomainUser(User user, HorairETSService service) {
        this.service = service;
        this.user = user;
        this.preferences = service.getPreferencesService().getPreferencesUtilisateur(user.getIdLong());
    }


    public void savePreferences() {
        service.getPreferencesService().savePreferencesUtilisateur(this.preferences);
    }


}
