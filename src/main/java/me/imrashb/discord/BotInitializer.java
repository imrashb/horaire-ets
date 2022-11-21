package me.imrashb.discord;

import me.imrashb.service.HorairETSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BotInitializer {

    private final HorairETSService mediator;

    @Value("${discord.token}")
    private String token;

    public BotInitializer(HorairETSService mediator) {
        this.mediator = mediator;
    }

    @Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
    public void initializeDiscordBot() {
        try {
            Bot bot = new Bot(token, mediator);
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'initialisation du bot Discord.");
            throw new RuntimeException(e);
        }
    }

}
