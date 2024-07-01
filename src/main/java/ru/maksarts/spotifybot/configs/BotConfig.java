package ru.maksarts.spotifybot.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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


    private AnswerInlineQuery lastAnswer = null; // TODO убрать наверное

    @Override
    public void onUpdateReceived(Update update) {
//        try {
//            log.info("webHook={}", getWebhookInfo().toString());
//            clearWebhook();
//        } catch (Exception ex) {
//            log.error("Exception while clearWebhook(): {}", ex.getMessage(), ex);
//        }
        if(update != null && update.hasMessage()) {
            log.info("message recieved={}, message={}", update.getMessage().getText(), update.getMessage().toString());
        }

        if(update != null && update.hasInlineQuery()){
            if (!update.getInlineQuery().getQuery().isBlank()) {
                log.info("inlineMessage={}, query={}", update.getInlineQuery().getQuery(), update.getInlineQuery().toString());
                botLogger.send("inline message=" + update.getInlineQuery().getQuery() + ", from=@" + update.getInlineQuery().getFrom().getUserName());

                try {
                    lastAnswer = inlineQueryHandler.handle(update.getInlineQuery());
                    // lastAnswer.setCacheTime(0);
                    execute(lastAnswer);
                } catch (Exception ex) {
                    log.error("Exception while handling inline query: {}", ex.getMessage(), ex);
                    botLogger.sendCode("Exception while handling inline query: " + ex.getMessage());
                }
            }
        }

        //TODO inline keyboard attache to the message
        if (update != null && update.hasCallbackQuery()) log.info("getCallbackQuery={}", update.getCallbackQuery());
        if (update != null && update.hasChosenInlineQuery()){
            log.info("getChosenInlineQuery={}", update.getChosenInlineQuery());

            if (lastAnswer != null) {
                InlineQueryResultArticle chosen = (InlineQueryResultArticle) lastAnswer.getResults().get(Integer.parseInt(update.getChosenInlineQuery().getResultId()));
                String songName = chosen.getTitle();
                String artist = chosen.getDescription();
                log.info("client has chosen: songName={}, artist={}", songName, artist);

//                log.info(lastAnswer.toString());

                lastAnswer = null;
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
