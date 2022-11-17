package me.imrashb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "preferences_utilisateur")
@Entity
public class PreferencesUtilisateur {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoris;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> horaires;

}
