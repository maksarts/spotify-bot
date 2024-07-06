package ru.maksarts.spotifybot.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.maksarts.spotifybot.handlers.TelegramInlineQueryHandler;
import ru.maksarts.spotifybot.services.VkService;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class BotConfig extends TelegramLongPollingBot {

    private static final String USERNAME = "SpotifyShareSongsBot";
    private static final String TOKEN = "token"; //TODO в проперти

    private final TelegramInlineQueryHandler inlineQueryHandler;
    private final BotLoggerConfig botLogger;

    public BotConfig(TelegramInlineQueryHandler inlineQueryHandler, BotLoggerConfig botLogger) {
        this.inlineQueryHandler = inlineQueryHandler;
        this.botLogger = botLogger;
    }


    private static final int bot_password = 1616890400; //TODO хранить в бд
    private boolean vkReAuthCommand = false;

    @Override
    public void onUpdateReceived(Update update) {

        //TODO кнопка реаутентификации в спотифайчике?

        if(update != null && update.hasMessage()) {
            log.info("message recieved={}, message={}", update.getMessage().getText(), update.getMessage().toString());

            if(update.getMessage().isCommand()){
                if(update.getMessage().getText().equals("/vkauth")){
                    log.warn("Vk re-auth request from user=@{}", update.getMessage().getFrom().getUserName());
                    SendMessage sm = SendMessage.builder()
                                                .chatId(update.getMessage().getChatId())
                                                .text("Please enter bot password")
                                                .build();
                    try {
                        execute(sm);
                    } catch (TelegramApiException e) {
                        log.error("TelegramApiException when sending message: {}", e.getMessage(), e);
                    }
                    vkReAuthCommand = true;
                }
            }

            else if(!update.getMessage().getText().isBlank() && vkReAuthCommand){
                vkReAuthCommand = false;
                String botPass = update.getMessage().getText().trim();
                if(botPass.hashCode() == bot_password){

                    try {

                        inlineQueryHandler.vkReAuth();
                        SendMessage sm = SendMessage.builder()
                                .chatId(update.getMessage().getChatId())
                                .text("Successfully authenticated in VK")
                                .build();
                        botLogger.send(String.format("User @%s evoked VK re-auth successfully", update.getMessage().getFrom().getUserName()));
                        try {
                            execute(sm);
                        } catch (TelegramApiException e) {
                            log.error("TelegramApiException when sending message: {}", e.getMessage(), e);
                        }

                    } catch (IOException ex) {
                        log.error("Exception while manual re-auth in VK: {}", ex.getMessage(), ex);
                        SendMessage sm = SendMessage.builder()
                                .chatId(update.getMessage().getChatId())
                                .text("Something went wrong:\n```" + ex.getMessage() + "```")
                                .build();
                        try {
                            execute(sm);
                        } catch (TelegramApiException e) {
                            log.error("TelegramApiException when sending message: {}", e.getMessage(), e);
                        }
                    }

                } else{
                    SendMessage sm = SendMessage.builder()
                            .chatId(update.getMessage().getChatId())
                            .text("Bruh, invalid bot password, go away man")
                            .build();
                    try {
                        execute(sm);
                    } catch (TelegramApiException e) {
                        log.error("TelegramApiException when sending message: {}", e.getMessage(), e);
                    }
                }
            }
        }

        if(update != null && update.hasInlineQuery()){
            if (!update.getInlineQuery().getQuery().isBlank()) {
                log.info("inlineMessage={}, query={}", update.getInlineQuery().getQuery(), update.getInlineQuery().toString());
                botLogger.send("inline message=" + update.getInlineQuery().getQuery() + ", from=@" + update.getInlineQuery().getFrom().getUserName());
                try {
                    AnswerInlineQuery answer = inlineQueryHandler.handle(update.getInlineQuery());
                    execute(answer);
                } catch (Exception ex) {
                    log.error("Exception while handling inline query: {}", ex.getMessage(), ex);
                    botLogger.sendCode("Exception while handling inline query: " + ex.getMessage());
                }
            }
        }

        if (update != null && update.hasCallbackQuery()) {
            log.info("getCallbackQuery={}", update.getCallbackQuery());
//            String inlineMessageId =  update.getCallbackQuery().getInlineMessageId();
//            String chatId =  update.getCallbackQuery().getChatInstance();
//            String data = update.getCallbackQuery().getData();
//
//            AnswerCallbackQuery close = AnswerCallbackQuery.builder()
//                    .callbackQueryId(update.getCallbackQuery().getId()).build();
//
//            try {
//                execute(close);
//            } catch (Exception ex) {
//                log.error("Exception while handling inline query: {}", ex.getMessage(), ex);
//                botLogger.sendCode("Exception while handling inline query: " + ex.getMessage());
//            }
        }
        if (update != null && update.hasChosenInlineQuery()){
//            log.info("getChosenInlineQuery={}", update.getChosenInlineQuery());
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
