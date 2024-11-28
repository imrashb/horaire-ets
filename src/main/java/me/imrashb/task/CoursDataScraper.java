package me.imrashb.task;

import me.imrashb.domain.CoursDataWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CoursDataScraper {

    private static final String URL_COURS_ETS = "https://www.etsmtl.ca/etudes/cours/";
    private boolean running = false;
    private String url;
    private String sigle;


    public CoursDataScraper(String sigle) {
        this.url = URL_COURS_ETS + sigle;
        this.sigle = sigle;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println(new CoursDataScraper("LOG100").getCoursData(Executors.newSingleThreadExecutor()).get());
    }

    private String getCoursTitle(Document doc) {
        Elements titre = doc.select("h1.o-title-2");
        if (titre.get(0) != null) {
            return titre.get(0).text();
        }
        return null;
    }

    private String getTextFromElementDescription(Document doc, String labelText) {
        Elements credits = doc.select("div.o-boxed-info__item");

        for (Element elem : credits) {
            Elements titleAndText = elem.children();
            if(titleAndText.size() != 2) continue;
            Element label = titleAndText.get(0);
            Element text = titleAndText.get(1);
            if (label.text().equalsIgnoreCase(labelText)) {
                return text.text();
            }

        }
        return null;
    }

    private Integer getCoursCredits(Document doc) {
        String text = getTextFromElementDescription(doc, "Crédits");

        if(text == null) {
            text = getTextFromElementDescription(doc, "Crédit");
        }

        return text != null ? Integer.parseInt(text) : 0;
    }

    private List<String> getCoursPrealables(Document doc) {
        String prealables = getTextFromElementDescription(doc, "Préalable(s)");
        return prealables != null ? Arrays.asList(prealables.split(" ")) : null;
    }

    private CoursDataWrapper scrape() {

        CoursDataWrapper data = new CoursDataWrapper();
        Document doc;

        try {
            doc = Jsoup.connect(this.url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        data.setSigle(this.sigle);
        data.setTitre(getCoursTitle(doc));
        data.setCredits(getCoursCredits(doc));
        data.setPrealables(getCoursPrealables(doc));
        return data;
    }

    public Future<CoursDataWrapper> getCoursData(final ExecutorService executor) {
        return executor.submit(this::scrape);
    }

}
