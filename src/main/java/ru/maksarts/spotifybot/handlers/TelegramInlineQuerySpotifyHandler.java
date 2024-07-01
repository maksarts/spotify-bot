package ru.maksarts.spotifybot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultAudio;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultDocument;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.maksarts.spotifybot.dto.types.*;
import ru.maksarts.spotifybot.services.SpotifyService;
import ru.maksarts.spotifybot.services.YoutubeService;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class TelegramInlineQuerySpotifyHandler implements TelegramInlineQueryHandler {
    @Autowired
    private SpotifyService spotifyService;
    @Autowired
    private YoutubeService youtubeService;

    private static final Pattern patternFile = Pattern.compile("(/file)");

    @Override
    public AnswerInlineQuery handle(InlineQuery inlineQuery){
        String query = inlineQuery.getQuery().toLowerCase(Locale.ROOT).trim();
        if (!query.isEmpty()) {
            if (query.charAt(0) == '/') {

                Matcher matcher = patternFile.matcher(query);
                if (matcher.find()) {
                    query = query.replaceAll("(/file )", "");
                    Track tracks = spotifyService.getTracks(query);
                    List<InlineQueryResult> results = makeRealSongsResults(tracks);
                    return convertResultsToResponse(inlineQuery, results);
                }

            } else {
                Track tracks = spotifyService.getTracks(query);
                List<InlineQueryResult> results = makeResults(tracks);

                //TODO тест кнопочек
//                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//                List<InlineKeyboardButton> row = new ArrayList<>();
//                InlineKeyboardButton button = new InlineKeyboardButton();
//                button.setText("кнопочка");
//                row.add(button);
//                rows.add(row);
//                inlineKeyboardMarkup.setKeyboard(rows);

                return convertResultsToResponse(inlineQuery, results);
            }
        }
        return convertResultsToResponse(inlineQuery, new ArrayList<>());
    }

    private static List<InlineQueryResult> makeSongsResults(Track tracks){
        List<InlineQueryResult> results = new ArrayList<>();
        ArrayList<Item> items = tracks.getItems();
        int i = 0;
        while (i < 20 && i < items.size()) {
            Item item = items.get(i);
            String artists = makeArtists(item.getArtists());
            String songName = item.getName();
            String spotifyUrl = item.getExternal_urls().getSpotify();

            InputTextMessageContent messageContent = new InputTextMessageContent();
            messageContent.setMessageText(spotifyUrl);

            InlineQueryResultAudio audio = new InlineQueryResultAudio();
            audio.setId(String.valueOf(i));
            audio.setTitle(songName);
            audio.setPerformer(artists);
            audio.setCaption(item.getExternal_urls().getSpotify());
            audio.setAudioUrl(item.getPreview_url());
            results.add(audio);
            i++;
            //log.info("Added to result: {} - {}", artists, songName);
        }
        return results;
    }

    private List<InlineQueryResult> makeRealSongsResults(Track tracks) {
        List<InlineQueryResult> results = new ArrayList<>();
        ArrayList<Item> items = tracks.getItems();
        int i = 0;

        // TODO сделать асинхронно поиск урлов
        while (i < 2 && i < items.size()) {
            Item item = items.get(i);
            String artists = makeArtists(item.getArtists());
            String songName = item.getName();
            String spotifyUrl = item.getExternal_urls().getSpotify();

            InputTextMessageContent messageContent = new InputTextMessageContent();
            messageContent.setMessageText(spotifyUrl);

            InlineQueryResultAudio audio = new InlineQueryResultAudio();
            audio.setId(String.valueOf(i));
            audio.setTitle(songName);
            audio.setPerformer(artists);
            audio.setCaption(item.getExternal_urls().getSpotify());

            //TODO не дает mp3, дает m4a
            String mainArtist = item.getArtists().get(0).getName();
            String videoUrl = youtubeService.getVideoUrl(mainArtist, songName);
            String audioUrl = youtubeService.getAudioUrl(videoUrl);
            audio.setAudioUrl(audioUrl);

            results.add(audio);
            i++;
        }
        return results;
    }

    private static List<InlineQueryResult> makeResults(Track tracks){
        List<InlineQueryResult> results = new ArrayList<>();
        ArrayList<Item> items = tracks.getItems();
        int i = 0;
        while (i < 20 && i < items.size()) {
            Item item = items.get(i);
            String artists = makeArtists(item.getArtists());
            String songName = item.getName();
            String spotifyUrl = item.getExternal_urls().getSpotify();
            String coverUrl = makeCover(item.getAlbum());

            InputTextMessageContent messageContent = new InputTextMessageContent();
            messageContent.setMessageText(spotifyUrl);

            InlineQueryResultArticle article = new InlineQueryResultArticle();
            article.setInputMessageContent(messageContent);
            article.setId(Integer.toString(i));
            article.setTitle(songName);
            article.setDescription(artists);
            article.setThumbUrl(coverUrl);
            results.add(article);
            i++;
            //log.info("Added to result: {} - {}", artists, songName);
        }
        return results;
    }

    private static String makeArtists(ArrayList<Artist> artists){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < artists.size(); i++){
            result.append(artists.get(i).getName());
            if(i < artists.size() - 1) result.append(", ");
        }
        return result.toString();
    }

    private static String makeCover(Album album){
        String result = "";
        Integer maxHeight = 0;
        ArrayList<Image> covers = album.getImages();
        for (Image cover : covers) {
            Integer height = cover.getHeight();
            if (height > maxHeight) {
                result = cover.getUrl();
                maxHeight = height;
            }
        }
        return result;
    }

    private static AnswerInlineQuery convertResultsToResponse(InlineQuery inlineQuery, List<InlineQueryResult> results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(100);
        answerInlineQuery.setResults(results);
//        answerInlineQuery.setSwitchPmText("To the chat");
//        answerInlineQuery.setSwitchPmParameter("pmParameter");
        return answerInlineQuery;
    }
}
