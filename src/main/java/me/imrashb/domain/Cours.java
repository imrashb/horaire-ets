package me.imrashb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cours {

    private String sigle;
    @JsonIgnore
    private List<Groupe> groupes;
    private Set<Programme> programmes;

    @JsonIgnore
    private Session session;


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
}
