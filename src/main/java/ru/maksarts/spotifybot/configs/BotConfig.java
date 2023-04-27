package ru.maksarts.spotifybot.configs;


import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Slf4j
public class BotConfig extends TelegramLongPollingBot {

    private static final String USERNAME = "SpotifyShareSongsBot";
    private static final String TOKEN = "5646977984:AAHiZjzxz-uWbdc0WNQzIXEoMghcg749oo8"; //TODO в инжекты

    @Override
    public void onUpdateReceived(Update update) {
        if(update.getMessage() != null) {
            log.info("message recieved={}", update.getMessage().getText());
        }

        if(update.hasInlineQuery()){
            log.info("inlineQuery={}", update.getInlineQuery().getQuery());

            String query = update.getInlineQuery().getQuery();
            try {
                if (!query.isEmpty()) {
                    List<String> resultsStr = Arrays.asList("Говно", "Залупа", "Пенис", "Хер");
                    List<InlineQueryResult> results = new ArrayList<>();
                    for (int i = 0; i < resultsStr.size(); i++) {
                        String str = resultsStr.get(i);
                        InputTextMessageContent messageContent = new InputTextMessageContent();
                        messageContent.setMessageText(str);

                        InlineQueryResultArticle article = new InlineQueryResultArticle();
                        article.setInputMessageContent(messageContent);
                        article.setId(Integer.toString(i));
                        article.setTitle(str.toUpperCase(Locale.ROOT));
                        article.setDescription("Жми сюда");
                        //article.setThumbUrl("https://lh5.ggpht.com/-kSFHGvQkFivERzyCNgKPIECtIOELfPNWAQdXqQ7uqv2xztxqll4bVibI0oHJYAuAas=w300");
                        results.add(article);
                    }
                    execute(converteResultsToResponse(update.getInlineQuery(), results));
                } else {
                    execute(converteResultsToResponse(update.getInlineQuery(), new ArrayList<>()));
                }
            } catch (TelegramApiException e) {
                log.error("ошибка", e);
            }
        }
    }

    private static AnswerInlineQuery converteResultsToResponse(InlineQuery inlineQuery, List<InlineQueryResult> results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(100);
        answerInlineQuery.setResults(results);
        return answerInlineQuery;
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
