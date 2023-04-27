package ru.maksarts.spotifybot.configs;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Controller
public class TelegramApiConfig {

    private TelegramBotsApi telegramBotsApi;

    @PostConstruct
    private void init(){
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new BotConfig());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
