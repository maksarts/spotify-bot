package ru.maksarts.spotifybot.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.maksarts.spotifybot.configs.credentials.LoggerbotProperties;
import ru.maksarts.spotifybot.configs.credentials.MainbotProperties;
import ru.maksarts.spotifybot.handlers.TelegramInlineQueryHandler;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
@EnableConfigurationProperties({MainbotProperties.class, LoggerbotProperties.class})
public class TelegramApiConfig {

    @Autowired
    private TelegramInlineQueryHandler inlineQueryHandler;
    @Autowired
    private MainbotProperties mainProps;
    @Autowired
    private LoggerbotProperties loggerProps;
    @Bean
    public TelegramBotsApi telegramBotsApi(){
        try {

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            BotLoggerConfig botLogger = new BotLoggerConfig(
                    loggerProps.getUsername(),
                    loggerProps.getToken(),
                    loggerProps.getChatId()
            );
            BotConfig botConfig = new BotConfig(
                    inlineQueryHandler,
                    botLogger,
                    mainProps.getUsername(),
                    mainProps.getToken()
            );

            telegramBotsApi.registerBot(botLogger);
            telegramBotsApi.registerBot(botConfig);

            return telegramBotsApi;

        } catch (TelegramApiException e) {
            log.error("Cannot create telegramBotsApi: {}", e.getMessage(), e);
            return null;
        }
    }
}
