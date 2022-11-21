package me.imrashb.discord.utils;

import lombok.Getter;
import me.imrashb.domain.PreferencesUtilisateur;
import me.imrashb.service.HorairETSService;
import net.dv8tion.jda.api.entities.User;

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
