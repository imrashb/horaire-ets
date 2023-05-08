package me.imrashb;

import me.imrashb.parser.calendrier.*;
import net.fortuna.ical4j.data.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

import java.io.*;
import java.text.*;
import java.util.concurrent.*;

public class MainNotifications {

    public static void main(String[] args) throws ParseException {

        new ETSCalendrierParser().parse();

    }

}