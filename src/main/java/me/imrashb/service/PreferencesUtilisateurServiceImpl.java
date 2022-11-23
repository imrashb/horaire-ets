package me.imrashb.service;

import me.imrashb.domain.PreferencesUtilisateur;
import me.imrashb.repository.PreferencesUtilisateurRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Scope("singleton")
public class PreferencesUtilisateurServiceImpl implements PreferencesUtilisateurService {

    private final PreferencesUtilisateurRepository repository;

    public PreferencesUtilisateurServiceImpl(PreferencesUtilisateurRepository repository) {
        this.repository = repository;
    }

    @Override
    public PreferencesUtilisateur getPreferencesUtilisateur(Long userId) {

        Optional<PreferencesUtilisateur> preferences = repository.findById(userId);

        return preferences.orElseGet(() -> new PreferencesUtilisateur(userId));
    }

    @Override
    public PreferencesUtilisateur savePreferencesUtilisateur(PreferencesUtilisateur preferences) {
        return repository.save(preferences);
    }

}
