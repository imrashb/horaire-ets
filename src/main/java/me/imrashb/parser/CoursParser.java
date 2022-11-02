package me.imrashb.parser;

import me.imrashb.domain.Activite;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.HeureActivite;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CoursParser {

    private static Pattern coursPattern = Pattern.compile("^([A-Z]{3}(\\d{3}|EST|TEST))\\s[A-Z]*\\s");
    private static Pattern groupePattern = Pattern.compile("^(\\d{2})?\\W*?([a-zA-Z]{3})\\W(\\d{2}):(\\d{2})\\W-\\W(\\d{2}):(\\d{2})\\W(.*)\\W.*(D|P|H|C)");

    private List<Cours> listeCours;
    private Cours currentCours = null;
    private Groupe currentGroupe = null;

    public CoursParser() {
        this.listeCours = new ArrayList<>();
    }

    public CoursParser(List<Cours> cours) {
        this.listeCours = cours;
    }

    public List<Cours> getCoursFromPDF(File f) throws IOException {

        currentCours = null;
        currentGroupe = null;

        String[] lines = getLinesFromPDF(f);


        for(String line : lines) {

            Cours cours = getCoursFromLine(line);

            if(handleCours(cours) || currentCours == null) {
                continue;
            }

            Groupe groupe = getGroupeFromLine(line);
            handleGroupe(groupe);

        }

        return listeCours;
    }

    public List<Cours> getCours() {
        return this.listeCours;
    }

    private boolean handleCours(Cours cours) {

        if(cours != null) {
            currentCours = cours;
            currentGroupe = null;

            if(!listeCours.contains(cours)) {
                listeCours.add(cours);
            }
            return true;
        }
        return false;
    }

    private boolean handleGroupe(Groupe groupe) {
        if(groupe != null) {
            currentGroupe = groupe;

            if(!currentCours.getGroupes().contains(groupe)) {
                currentCours.getGroupes().add(groupe);
            }
            return true;
        }

        return false;
    }

    private Cours getCoursFromLine(String line) {

        Matcher match = coursPattern.matcher(line);

        if(match.find()) {

            String id = match.group(1);

            for(Cours cours : listeCours) {
                // Si cours existe deja retourne le cours
                if(cours.getSigle().equalsIgnoreCase(id)) {
                    return cours;
                }
            }

            Cours cours = new Cours(id, new ArrayList<>());
            return cours;
        }
        return null;
    }


    private Groupe getGroupeFromLine(String line) {

        Object obj = getGroupe(line);

        if(obj instanceof Groupe) return (Groupe) obj;

        if(obj instanceof Activite) {

            Activite activite = (Activite) obj;

            for(Activite a : currentGroupe.getActivites()) {
                if(a.equals(activite)) return currentGroupe;
            }

            currentGroupe.addActivite(activite);
            return currentGroupe;
        }

        return null;
    }

    private Object getGroupe(String line) {
        Matcher match = groupePattern.matcher(line);

        // Check si definition du cours
        if(match.find()) {

            String id = match.group(1);

            HeureActivite sch = new HeureActivite(
                    Integer.parseInt(match.group(3)),
                    Integer.parseInt(match.group(4)),
                    Integer.parseInt(match.group(5)),
                    Integer.parseInt(match.group(6)),
                    match.group(2));

            Activite activite = new Activite(match.group(7), match.group(8), sch);

            if(match.group(1) == null) {
                return activite;
            }

            // Verifie si groupe existe deja
            for(Groupe g : currentCours.getGroupes()) {
                if(g.getNumeroGroupe().equalsIgnoreCase(id)) {

                    for(Activite a : g.getActivites()) {
                        if(a.equals(activite)) return g;
                    }

                    g.addActivite(activite);
                    return g;
                }
            }

            Groupe groupe = new Groupe(id, new ArrayList<Activite>(), currentCours);
            groupe.addActivite(activite);
            return groupe;
        }
        return null;
    }
    private String[] getLinesFromPDF(File f) throws IOException {

        PDDocument document = PDDocument.load(f);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text.split("\\R");

    }


}
