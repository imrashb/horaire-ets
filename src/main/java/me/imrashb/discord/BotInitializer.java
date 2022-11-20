package me.imrashb.discord;

import me.imrashb.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

@Component
@EnableScheduling
public class BotInitializer {

    @Autowired
    private HorairETSService mediator;

    @Value("${discord.token}")
    private String token;

    @Scheduled(initialDelay = 0, fixedDelay=Long.MAX_VALUE)
    public void initializeDiscordBot() {
        try {
            Bot bot = new Bot(token, mediator);
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'initialisation du bot Discord.");
            throw new RuntimeException(e);
        }
    }

}
