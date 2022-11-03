package me.imrashb;
import me.imrashb.domain.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class Main {

    public static int pdfCount=0;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /*
    public static void main(String[] args) throws IOException {
        CoursParser parser = new CoursParser();

        List<File> files = null;
        try {
            files = ETSUtils.getFichiersHoraireSync(2022, Trimestre.AUTOMNE);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(File f : files) {
            parser.getCoursFromPDF(f);
            f.delete();
        }

        List<Cours> listeCours = parser.getCours();


        for(Cours c : listeCours) {
            for(Groupe g : c.getGroupes()) {
                System.out.println(g);
            }
        }

        List<CombinaisonHoraire> combinaisons = new GenerateurHoraire(listeCours).getCombinaisonsHoraire("LOG121", "LOG240", "MAT350", "PHY335");

        printCombinaisons(combinaisons);
        System.out.println("Nombre de combinaisons: "+combinaisons.size());

    }

     */

    public static void printCombinaisons(List<CombinaisonHoraire> combinaisons) {
        for(CombinaisonHoraire combinaison : combinaisons) {
            String[][] tab = new String[7][3];
            for(Groupe g : combinaison.getGroupes()) {
                int count = 0;
                for(Jour jour : Jour.values()) {
                    for(int i = 0; i<tab[0].length; i++) {

                        for(Activite a : g.getActivites()) {

                            HoraireActivite sch;
                            if(i == 0) {
                                sch = new HoraireActivite(6, 0, 12, 0, jour.getNom().substring(0, 3));
                            } else if(i == 1) {
                                sch = new HoraireActivite(13, 0, 17, 0, jour.getNom().substring(0, 3));
                            } else {
                                sch = new HoraireActivite(18, 0, 22, 0, jour.getNom().substring(0, 3));
                            }

                            if(sch.overlapsWith(a.getHoraire())) {
                                tab[count][i] = g.getCours().getSigle()+"-"+g.getNumeroGroupe();
                            }
                        }
                    }
                    count++;
                }
            }

            System.out.println(" LUNDI    MARDI        MERCREDI   JEUDI      VENDREDI   SAMEDI     DIMANCHE");
            for(int i = 0; i<tab[0].length; i++) {
                for(int j = 0; j<tab.length; j++) {
                    System.out.print(" ");
                    System.out.print(tab[j][i] == null ? "---------" : tab[j][i] );
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

}