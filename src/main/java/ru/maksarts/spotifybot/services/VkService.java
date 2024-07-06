package ru.maksarts.spotifybot.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.maksarts.spotifybot.dto.VkAuthInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VkService {

    //TODO добавить автоматическую(?) переаутентификацию в вк
    public VkService() throws IOException {
        vkAuth();
    }

    protected static Pattern patternAudioUrl;

    static {
        patternAudioUrl = Pattern.compile("(https://)");
    }

    private VkAuthInfo vkAuthInfo = null;

    //TODO обернуть в питоне в трай кеч и при выпадании ошибки сделать реаус автоматом?
    public void vkAuth() throws IOException {
        log.info("Authentication in VK");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python",
                                                                        "pyscripts/vk_audio_auth.py",
                                                                        "--login", "login",
                                                                        "--password", "password"); //TODO в проперти
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            ArrayList<String> output = new ArrayList<>();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bufferedWriter.write("OK");
            bufferedWriter.flush();
            bufferedWriter.close();

            String line = bufferedReader.readLine();
            while (line != null) {
                log.info(line);
                output.add(line);
                line = bufferedReader.readLine();
            }

            for(String out : output){
                try{
                    vkAuthInfo = new Gson().fromJson(out, VkAuthInfo.class);
                } catch (Exception ignored){ }
            }

            if (vkAuthInfo == null){
                throw new RuntimeException("Cannot get token and userAgent from VK. Full output: " + output);
            }

        } catch (Exception ex){
            throw new IOException("Exception while authentication in VK: {}", ex);
        }
    }

    public String getAudioUrl(String artist, String songName) {
        String url = null;
        try {

            ProcessBuilder processBuilder = new ProcessBuilder("python",
                                                                        "pyscripts/vk_audio_script.py",
                                                                        "--query", artist + " - " + songName,
                                                                        "--token", vkAuthInfo.getToken(),
                                                                        "--user_agent", vkAuthInfo.getUserAgent());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = bufferedReader.readLine();
            while (line != null) {

                Matcher matcher = patternAudioUrl.matcher(line);
                if(matcher.find()){
                    url = line;
                }

                log.info(line);
                line = bufferedReader.readLine();
            }
            if(url != null) {
                //log.info("Audio url found successfully: url={}", url);
            }
            else{
                log.warn("Audio url is null, query={} - {}", artist, songName);
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

        return url;
    }
}
