package me.imrashb.service;

import me.imrashb.domain.PreferencesUtilisateur;

public interface PreferencesUtilisateurService {

    PreferencesUtilisateur getPreferencesUtilisateur(Long userId);

    PreferencesUtilisateur savePreferencesUtilisateur(PreferencesUtilisateur preferences);

}
