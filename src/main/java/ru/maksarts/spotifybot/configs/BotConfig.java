package ru.maksarts.spotifybot.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maksarts.spotifybot.handlers.TelegramInlineQueryHandler;

@Slf4j
public class BotConfig extends TelegramLongPollingBot {

    private static final String USERNAME = "SpotifyShareSongsBot";
    private static final String TOKEN = "<token>"; //TODO в проперти

    private final TelegramInlineQueryHandler inlineQueryHandler;
    private final BotLoggerConfig botLogger;

    public BotConfig(TelegramInlineQueryHandler inlineQueryHandler, BotLoggerConfig botLogger) {
        this.inlineQueryHandler = inlineQueryHandler;
        this.botLogger = botLogger;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update != null && update.hasMessage()) {
            log.info("message recieved={}, message={}", update.getMessage().getText(), update.getMessage().toString());
        }

        if(update != null && update.hasInlineQuery()){
            if (!update.getInlineQuery().getQuery().isBlank()) {
                log.info("inlineMessage={}, query={}", update.getInlineQuery().getQuery(), update.getInlineQuery().toString());
                botLogger.send("inline message=" + update.getInlineQuery().getQuery() + ", from=@" + update.getInlineQuery().getFrom().getUserName());
            }
            try {
                AnswerInlineQuery answer = inlineQueryHandler.handle(update.getInlineQuery());
                execute(answer);
            } catch (Exception ex){
                log.error("Exception while handling inline query: {}", ex.getMessage(), ex);
                botLogger.sendCode("Exception while handling inline query: " + ex.getMessage());
            }
        }
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }
}
