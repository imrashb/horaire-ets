package me.imrashb.service;

import me.imrashb.domain.PreferencesUtilisateur;
import me.imrashb.repository.PreferencesUtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Scope("singleton")
public class PreferencesUtilisateurServiceImpl implements PreferencesUtilisateurService {

    @Autowired
    private PreferencesUtilisateurRepository repository;

    @Override
    public PreferencesUtilisateur getPreferencesUtilisateur(Long userId) {

        Optional<PreferencesUtilisateur> preferences = repository.findById(userId);

        if(preferences.isPresent()) {
            return preferences.get();
        } else {
            return new PreferencesUtilisateur(userId);
        }
    }

    @Override
    public PreferencesUtilisateur savePreferencesUtilisateur(PreferencesUtilisateur preferences) {
        return repository.save(preferences);
    }

}
