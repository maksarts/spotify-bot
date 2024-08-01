package ru.maksarts.spotifybot.handlers;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.io.IOException;

public interface TelegramInlineQueryHandler {
    public AnswerInlineQuery handle(InlineQuery inlineQuery);
    public void vkReAuth() throws IOException;
}
