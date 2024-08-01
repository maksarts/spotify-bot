package ru.maksarts.spotifybot.handlers;

import javassist.LoaderClassPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
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
import ru.maksarts.spotifybot.services.VkService;
import ru.maksarts.spotifybot.services.YoutubeService;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class TelegramInlineQuerySpotifyHandler implements TelegramInlineQueryHandler {
    @Autowired
    private SpotifyService spotifyService;
    @Autowired
    private VkService vkService;

    private static final Pattern patternFile = Pattern.compile("(/link)");
    private LocalDateTime lastTimestamp = LocalDateTime.now();

    public void vkReAuth() throws IOException {
        vkService.vkAuth();
    }

    @Override
    public AnswerInlineQuery handle(InlineQuery inlineQuery){
        String query = inlineQuery.getQuery().toLowerCase(Locale.ROOT).trim();
        if (!query.isEmpty()) {
            // чтобы обрабатывались запросы не чаще чем раз в 3 сек
            if (lastTimestamp.plusSeconds(3).isBefore(LocalDateTime.now())) {
                lastTimestamp = LocalDateTime.now();
                if (query.charAt(0) == '/') {

                    Matcher matcher = patternFile.matcher(query);
                    if (matcher.find()) {
                        query = query.replaceAll("(/link )", "");
                        Track tracks = spotifyService.getTracks(query);
                        List<InlineQueryResult> results = makeResults(tracks);
                        return convertResultsToResponse(inlineQuery, results);
                    }

                } else {
                    Track tracks = spotifyService.getTracks(query);
                    List<InlineQueryResult> results = makeRealSongsResults(tracks);
                    return convertResultsToResponse(inlineQuery, results);
                }
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
        }
        return results;
    }

    private List<InlineQueryResult> makeRealSongsResults(Track tracks) {
        List<InlineQueryResult> results = new ArrayList<>();
        ArrayList<Item> items = tracks.getItems();

        int i = 0;
        while (i < 5 && i < items.size()) {
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

            InlineKeyboardButton urlButton = InlineKeyboardButton.builder()
                    .text("Open in Spotify")
                    .url(item.getExternal_urls().getSpotify())
                    .build();

            InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(urlButton))
                    .build();

            audio.setReplyMarkup(keyboard);

            results.add(audio);
            i++;
        }

        results = results.parallelStream().peek(result -> {
            String artists = ((InlineQueryResultAudio) result).getPerformer();
            String songName = ((InlineQueryResultAudio) result).getTitle();
            String audioUrl = vkService.getAudioUrl(artists, songName);
            if(audioUrl != null) {
                ((InlineQueryResultAudio) result).setAudioUrl(audioUrl);
            }
        }).filter(result -> ((InlineQueryResultAudio) result).getAudioUrl() != null).collect(Collectors.toList());
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
