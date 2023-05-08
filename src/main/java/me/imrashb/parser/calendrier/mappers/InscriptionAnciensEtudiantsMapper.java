package me.imrashb.parser.calendrier.mappers;

import me.imrashb.parser.calendrier.*;
import org.apache.commons.lang3.*;
import org.jsoup.nodes.*;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;


public class InscriptionAnciensEtudiantsMapper extends CalendrierMapper {
    public InscriptionAnciensEtudiantsMapper(ETSCalendrier calendrier) {
        super(calendrier, CalendrierEvent.Type.INSCRIPTION_ANCIENS_ETUDIANTS, "^Période d’inscription$");
    }

    @Override
    protected LocalDate mapDate(Element element) throws ParseException {

        Pattern pattern = Pattern.compile("^Anciens étudiants :(?: du)? (.*) \\(.*\\) au (.*) (\\d{4})$");

        for(Element listElement : element.children()) {
            Matcher match = pattern.matcher(listElement.text());
            if(match.find()) {

                final String[] jourMois = match.group(1).split(" ");
                final int jour = Integer.parseInt(jourMois[0]);
                final String mois = jourMois[1];
                final int annee = Integer.parseInt(match.group(3));

                return getDate(jour, mois, annee);

            }
        }

        return null;
    }
}
