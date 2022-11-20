package me.imrashb.parser.strategy;

import me.imrashb.domain.*;

import java.util.*;

public class GroupeOverlapStrategy implements HoraireValidationStrategy {


    @Override
    public boolean isValid(List<Groupe> current, Groupe groupe) {
        for(Groupe g : current) {
            if(g.overlapsWith(groupe)) return false;
        }
        return true;
    }
}
