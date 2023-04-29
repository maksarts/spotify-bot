package ru.maksarts.spotifybot.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BotLoggerConfig extends TelegramLongPollingBot {

    private static final String USERNAME = "SpotifyShareSongsLoggerBot";
    private static final String TOKEN = "<token>"; //TODO в проперти

    private static final String CHAT_ID = "399101922";

    @Override
    public void onUpdateReceived(Update update) {
        if(update != null && update.hasMessage()) {
            log.info("message recieved={}, message={}", update.getMessage().getText(), update.getMessage().toString());
        }
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    public void sendCode(String logText){
        send("<code>" + logText + "</code>");
    }

    public void send(String logText){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(logText);
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            log.error("Cannot send log: {}", ex.getMessage(), ex);
        }
    }
}
