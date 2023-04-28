package ru.maksarts.spotifybot.configs;


import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maksarts.spotifybot.handlers.TelegramInlineQueryHandler;

import java.util.*;

@Slf4j
public class BotConfig extends TelegramLongPollingBot {

    private static final String USERNAME = "SpotifyShareSongsBot";
    private static final String TOKEN = "<token>"; //TODO в проперти

    private final TelegramInlineQueryHandler inlineQueryHandler;

    public BotConfig(TelegramInlineQueryHandler inlineQueryHandler) {
        this.inlineQueryHandler = inlineQueryHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.getMessage() != null) {
            log.info("message recieved={}", update.getMessage().getText());
        }

        if(update.hasInlineQuery()){
            log.info("inlineMessage={}, query={}", update.getInlineQuery().getQuery(), update.getInlineQuery().toString());
            try {
                AnswerInlineQuery answer = inlineQueryHandler.handle(update.getInlineQuery());
                execute(answer);
            } catch (TelegramApiException ex){
                log.error("Exception while handling inline query: {}", ex.getMessage(), ex);
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
