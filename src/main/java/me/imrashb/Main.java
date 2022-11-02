package me.imrashb;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    public static int pdfCount=0;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        final String trimestre = "20231";
        final String[] idProgrammes = {
                "SEG", // Enseignements generaux
                "7084", // LOG
                "5730", // CUT
                "7625", // CTN
                "7694", // ELE
                "7684", // MEC
                "6556", // GOL
                "6557", // GPA
        };

        CoursParser parser = new CoursParser();

        List<File> files = getETSFiles(idProgrammes, trimestre);

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

        List<List<Groupe>> combinaisons = generateCombinaisons(listeCours, "GIA410", "LRC100", "ING500", "GTS615");

        printCombinaisons(combinaisons);
        System.out.println("Nombre de combinaisons: "+combinaisons.size());

    }

    public static List<File> getETSFiles(String[] idProgrammes, String trimestre) {
        final String path = "./pdf";
        for(String programme : idProgrammes) {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://horaire.etsmtl.ca/HorairePublication/HorairePublication_"+trimestre+"_"+programme+".pdf");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }

                    File file = new File(path+"/"+programme+".pdf");
                    try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        byte dataBuffer[] = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        System.out.println("ERREUR");
                    }
                    pdfCount++;
                }
            };

            new Thread(run).start();

        }

        while(pdfCount < idProgrammes.length) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        List<File> files = new ArrayList<>();
        for(String programme : idProgrammes) {
            File file = new File(path+"/"+programme+".pdf");
            files.add(file);
        }

        return files;
    }

    public static void printCombinaisons(List<List<Groupe>> combinaisons) {
        for(List<Groupe> liste : combinaisons) {
            String[][] tab = new String[7][3];
            for(Groupe g : liste) {
                int count = 0;
                for(Jour jour : Jour.values()) {
                    for(int i = 0; i<tab[0].length; i++) {

                        for(Activite a : g.getActivites()) {

                            Schedule sch;
                            if(i == 0) {
                                sch = new Schedule(6, 0, 12, 0, jour.getNom().substring(0, 3));
                            } else if(i == 1) {
                                sch = new Schedule(13, 0, 17, 0, jour.getNom().substring(0, 3));
                            } else {
                                sch = new Schedule(18, 0, 22, 0, jour.getNom().substring(0, 3));
                            }

                            if(sch.overlapsWith(a.getSchedule())) {
                                tab[count][i] = g.getCours().getId()+"-"+g.getId();
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

    public static List<List<Groupe>> generateCombinaisons(List<Cours> listeCours, String... nomCours) {

        List<Cours> coursVoulu = new ArrayList<>();

        for (Cours c : listeCours) {
            for (String s : nomCours) {
                if (c.getId().equalsIgnoreCase(s)) coursVoulu.add(c);
            }
        }

        NodeGroupe node = new NodeGroupe(null, null);

        recurCreateCombinaisons(coursVoulu, 0, node);

        return node.getValidCombinaisons(coursVoulu);

    }

    private static void recurCreateCombinaisons(List<Cours> cours, int index, NodeGroupe node) {

        if(index == cours.size()) {
            return;
        }

        Cours courant = cours.get(index);

        for(Groupe g : courant.getGroupes()) {

            if(!node.isOverlapping(g)) {
                NodeGroupe n = node.createNode(g);
                recurCreateCombinaisons(cours, index+1, n);
            }

        }

    }

}