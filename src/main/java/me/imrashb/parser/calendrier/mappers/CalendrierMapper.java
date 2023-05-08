package me.imrashb.parser.calendrier.mappers;

import me.imrashb.parser.calendrier.*;
import org.jsoup.nodes.*;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

public abstract class CalendrierMapper {
    private List<Pattern> patterns;
    private CalendrierEvent.Type type;

    private ETSCalendrier calendrier;

    public CalendrierMapper(ETSCalendrier calendrier, CalendrierEvent.Type type, String... pattern) {
        this.patterns = new ArrayList<>();
        Arrays.stream(pattern).forEach((p) -> patterns.add(Pattern.compile(p)));
        this.type = type;
        this.calendrier = calendrier;
    }

    public boolean isMatchingPattern(String value) {
        return patterns.stream().anyMatch((p) -> p.matcher(value).find());
    }

    protected abstract LocalDate mapDate(Element element) throws ParseException;

    public void createCalendrierEvent(Element element, String description) throws ParseException {
        LocalDate date = this.mapDate(element);
        if(date != null) calendrier.addEvent(new CalendrierEvent(this.type, date, description));
    }

    protected LocalDate getDate(int jour, String mois, int annee) {

        Optional<Month> opt = Arrays.stream(Month.values()).filter(
                (m) -> m.getDisplayName(TextStyle.FULL, Locale.CANADA_FRENCH).equalsIgnoreCase(mois)
        ).findFirst();
        Month month = opt.get();

        return LocalDate.of(annee, month, jour);

    }

}