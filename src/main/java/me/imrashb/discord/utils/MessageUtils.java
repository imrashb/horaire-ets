package me.imrashb.discord.utils;

import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MessageUtils {

    public static FileUpload getFileUploadFromImage(Image image, String name) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write((RenderedImage) image, "jpeg", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return FileUpload.fromData(os.toByteArray(), name);
    }

    public static String bold(String text) {
        return "**"+text+"**";
    }

    public static String italic(String text) {
        return "*"+text+"*";
    }

    public static String underline(String text) {
        return "__"+text+"__";
    }
}
