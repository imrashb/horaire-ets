package me.imrashb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Table(name = "preferences_utilisation")
@Entity
public class PreferencesUtilisateur {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoris;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> horaires;

    @Column(name = "theme_id")
    private String themeId = null;

    @Column(name = "is_private", columnDefinition = "boolean default true")
    private boolean isPrivate = true;

    public PreferencesUtilisateur(Long userId) {
        this.userId = userId;
        this.horaires = new HashMap<>();
        this.favoris = new ArrayList<>();
    }

}
