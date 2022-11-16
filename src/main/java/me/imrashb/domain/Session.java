package me.imrashb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Session {

    private int annee;
    private Trimestre trimestre;

    public int toId() {
        return Integer.parseInt(""+this.annee+trimestre.getNumeroSession());
    }

    public String toString() {
        return ""+trimestre.getLettre()+this.annee;
    }

}
