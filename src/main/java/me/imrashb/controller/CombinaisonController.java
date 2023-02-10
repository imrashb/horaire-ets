package me.imrashb.controller;

import me.imrashb.domain.*;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.*;
import me.imrashb.exception.*;
import me.imrashb.service.CombinaisonService;
import me.imrashb.utils.*;
import org.apache.pdfbox.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/combinaisons")
public class CombinaisonController {

    private final CombinaisonService service;

    public CombinaisonController(CombinaisonService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<CombinaisonHoraire> getCombinaisonsHoraire(ParametresCombinaison parametres) throws RuntimeException {

        CombinaisonHoraireComparator comparator = null;

        if(parametres.getSort() != null && parametres.getSort().size() > 0) {
            CombinaisonHoraireComparator.Builder builder = new CombinaisonHoraireComparator.Builder();
            for(CombinaisonHoraireComparator.Comparator c : parametres.getSort()) {
                try {
                    builder.addComparator(c.getComparatorClass());
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            comparator = builder.build();
        }


        List<CombinaisonHoraire> combinaisons = service.getCombinaisonsHoraire(parametres);

        if(comparator != null) Collections.sort(combinaisons, comparator);

        return combinaisons;
    }

    @GetMapping("sort")
    public CombinaisonHoraireComparator.Comparator[] getCombinaisonHoraireSorters() {
        return service.getAvailableCombinaisonHoraireComparators();
    }

    @GetMapping(value="{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getCombinaisonImage(@PathVariable String id, @RequestParam(required = false) String theme) throws ExecutionException, InterruptedException, IOException {

        HoraireImageMakerTheme imageTheme = HoraireImageMaker.getThemeFromId(theme);

        CombinaisonHoraire comb = service.getCombinaisonFromEncodedId(id);
        Future<Image> future = new HoraireImageMaker(comb, imageTheme).drawHoraire();
        Image img = future.get();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) img,"jpeg", os);
        return os.toByteArray();
    }

}
