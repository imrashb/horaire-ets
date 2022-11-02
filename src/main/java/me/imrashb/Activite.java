package me.imrashb;

import lombok.*;

@Data
@AllArgsConstructor
public class Activite {

    private String nom;
    private String type;
    private Schedule schedule;

    public String toString() {
        return this.nom +" "+schedule;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Activite) {
            Activite a = (Activite) obj;

            if(this.nom.equalsIgnoreCase(a.nom) && this.type.equalsIgnoreCase(a.type) && this.schedule.equals(a.schedule)) {
                return true;
            }
        }
        return false;
    }

}
