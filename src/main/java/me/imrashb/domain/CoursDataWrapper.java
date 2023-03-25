package me.imrashb.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Table(name = "cours_data")
@Entity
public class CoursDataWrapper {
    @Id
    @Column(name = "sigle", nullable = false)
    private String sigle;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> prealables;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "titre", nullable = false)
    private String titre;

}
