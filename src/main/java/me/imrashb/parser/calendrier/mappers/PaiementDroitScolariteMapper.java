package me.imrashb.parser.calendrier.mappers;

import me.imrashb.parser.calendrier.*;
import org.jsoup.nodes.*;

import java.text.*;
import java.time.*;
import java.util.regex.*;


public class PaiementDroitScolariteMapper extends CalendrierMapper {
    public PaiementDroitScolariteMapper(ETSCalendrier calendrier) {
        super(calendrier, CalendrierEvent.Type.PAIEMENT_DROITS_SCOLARITE,"^Paiement des droits de scolarité$", "^Paiement des frais de scolarité$");
    }

    @Override
    protected LocalDate mapDate(Element element) throws ParseException {


        Pattern pattern = Pattern.compile("^Date limite pour le 1er paiement : (\\d{1,2}) (.*) (\\d{4})$");

        for(Element listElement : element.children()) {
            Matcher match = pattern.matcher(listElement.text());
            if(match.find()) {
                final int jour = Integer.parseInt(match.group(1));
                final String mois = match.group(2);
                final int annee = Integer.parseInt(match.group(3));

                return getDate(jour, mois, annee);

            }
        }

        return null;
    }
}
