package me.imrashb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@RequiredArgsConstructor
@ToString
public class Cours {

    private final String sigle;
    @JsonIgnore
    private final List<Groupe> groupes;
    private final Set<Programme> programmes;

    @JsonIgnore
    private final Session session;

    private Integer credits = null;

    private List<String> prealables;

    private String titre;


    public void addProgramme(Programme programme) {
        this.programmes.add(programme);
    }

    public void addGroupe(Groupe groupe) {
        this.groupes.add(groupe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sigle, programmes);
    }

    public void syncFromCoursData(CoursDataWrapper data) {
        this.credits = data.getCredits();
        this.prealables = data.getPrealables();
        this.titre = data.getTitre();
    }
}
