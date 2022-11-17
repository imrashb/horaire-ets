package me.imrashb.discord.utils;

import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.utils.HoraireImageMaker;
import me.imrashb.utils.HoraireImageMakerTheme;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class MessageUtils {

    public static void partagerImageHoraire(Interaction event,
                                            CombinaisonHoraire combinaison,
                                            HoraireImageMakerTheme theme,
                                            Consumer<? super Message> consumer) {
        Image image = new HoraireImageMaker(combinaison, theme).drawHoraire();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write((RenderedImage) image, "jpeg", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageCreateAction action = event.getMessageChannel()
                .sendMessage(":newspaper: Horaire partagée par <@"+event.getUser().getIdLong()+"> :newspaper:")
                .setFiles(FileUpload.fromData(os.toByteArray(), combinaison.getUniqueId()+".jpeg"))
                .mention(event.getUser());
        if(consumer == null) action.queue();
        else action.queue(consumer);
    }

}
