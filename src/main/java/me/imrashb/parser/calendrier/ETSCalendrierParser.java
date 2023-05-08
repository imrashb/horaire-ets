package me.imrashb.parser.calendrier;

import lombok.*;
import me.imrashb.domain.*;
import me.imrashb.parser.calendrier.mappers.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class ETSCalendrierParser {

    private static final String PROPERTY_SUMMARY = "SUMMARY";
    private static final String PROPERTY_DATE = "DTSTART";
    private static final String PROPERTY_UID = "UID";

    private final List<CalendrierMapper> mappers;

    private final ETSCalendrier calendrier;

    public ETSCalendrierParser() {
        this.calendrier = new ETSCalendrier();
        this.mappers = new ArrayList<>();
        initMappers();
    }

    private void initMappers() {
        this.mappers.add(new InscriptionAnciensEtudiantsMapper(calendrier));
        this.mappers.add(new PermutationHoraireMapper(calendrier));
        this.mappers.add(new DebutSessionMapper(calendrier));
        this.mappers.add(new FinSessionMapper(calendrier));
        this.mappers.add(new PaiementDroitScolariteMapper(calendrier));
    }

    public ETSCalendrier parse() throws ParseException {

        final String url = "https://www.etsmtl.ca/etudes/calendrier-universitaire";

        Document doc;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Element mainContent = doc.selectFirst("#main-content");

        Elements containers = mainContent.select(".container");

        for(Element container : containers) {

            SessionElements elements = getSessionElements(container);

            if(elements == null) continue;

            for(Element elem : elements.getElements().select("h3")) {
                for(CalendrierMapper mapper : mappers) {
                    if(mapper.isMatchingPattern(elem.text())) {
                        mapper.createCalendrierEvent(elem.nextElementSibling(), elem.text());
                    }
                }
            }

        }

        return calendrier;
    }

    private SessionElements getSessionElements(Element container) {
        Element collapse = container.selectFirst(".collapse");

        if(collapse == null) return null;

        Element anchor = collapse.selectFirst("a");
        Element sessionElem = anchor.selectFirst("h2");
        Session session = getSessionFromName(sessionElem.text());

        if(session == null) return null;

        Elements elements = collapse.selectFirst(".collapse__content").children();

        return new SessionElements(session.toString(), elements);
    }

    private Session getSessionFromName(String name) {

        try {
            final String[] split = name.split(" ");
            final String nomSession = split[0];
            final int anneeSession = Integer.parseInt(split[1]);
            switch(nomSession.toLowerCase()) {
                case "hiver": return Trimestre.HIVER.getSession(anneeSession);
                case "été": return Trimestre.ETE.getSession(anneeSession);
                case "automne": return Trimestre.AUTOMNE.getSession(anneeSession);
                default: return null;
            }

        } catch(Exception ex) {
            return null;
        }
    }

    @AllArgsConstructor
    @Getter
    private class SessionElements {
        private String sessionId;
        private Elements elements;
    }

}
