package me.imrashb.parser.calendrier;

import lombok.*;

import java.util.*;

public class ETSCalendrier {

    @Getter
    private final List<CalendrierEvent> events;

    public ETSCalendrier() {
        this.events = new ArrayList<>();
    }

    public void addEvent(CalendrierEvent event) {
        System.out.println(event);
        this.events.add(event);
    }

}


