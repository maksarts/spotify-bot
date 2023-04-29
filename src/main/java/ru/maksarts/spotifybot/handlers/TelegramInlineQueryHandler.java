package ru.maksarts.spotifybot.handlers;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public interface TelegramInlineQueryHandler {
    public AnswerInlineQuery handle(InlineQuery inlineQuery);
}
