package ru.maksarts.spotifybot.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.maksarts.spotifybot.configs.credentials.VkProperties;
import ru.maksarts.spotifybot.dto.VkAuthInfo;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@EnableConfigurationProperties(VkProperties.class)
public class VkService {

    //TODO добавить автоматическую(?) переаутентификацию в вк
    @PostConstruct
    public void postConstructInitialisation() throws IOException {
        try {
            vkAuth();
        } catch (Exception ex){
            log.error("Cannot auth in VK: {}", ex.getMessage(), ex);
        }
    }

    protected static Pattern patternAudioUrl;
    protected static Pattern patternToken;

    static {
        patternAudioUrl = Pattern.compile("(https://)");
        patternToken = Pattern.compile("(token)");
    }

    private VkAuthInfo vkAuthInfo = null;

    @Autowired
    private VkProperties props;

    //TODO обернуть в питоне в трай кеч и при выпадании ошибки сделать реаус автоматом?
    public void vkAuth() throws IOException {
        log.info("Authentication in VK");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python",
                                                                        "pyscripts/vk_audio_auth.py",
                                                                        "--login", props.getLogin(),
                                                                        "--password", props.getPassword());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bufferedWriter.write("OK");
            bufferedWriter.flush();
            bufferedWriter.close();

            StringBuilder output = new StringBuilder();
            Matcher matcher;
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line).append("\n");
                try{
                    vkAuthInfo = new Gson().fromJson(line, VkAuthInfo.class);
                } catch (Exception ignored){ }
                matcher = patternToken.matcher(line);
                if(!matcher.find()){
                    log.info(line);
                } else {
                    log.info("VK token found");
                }
                line = bufferedReader.readLine();
            }


            if (vkAuthInfo == null){
                throw new RuntimeException("Cannot get token and userAgent from VK. Full output: " + output);
            }

        } catch (Exception ex){
            throw new IOException(String.format("Exception while authentication in VK: %s", ex.getMessage()), ex);
        }
    }

    public String getAudioUrl(String artist, String songName) {
        String url = null;
        if(vkAuthInfo != null) {
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
                    if (matcher.find()) {
                        url = line;
                    }

                    log.info(line);
                    line = bufferedReader.readLine();
                }
                if (url != null) {
                    //log.info("Audio url found successfully: url={}", url);
                } else {
                    log.warn("Audio url is null, query={} - {}", artist, songName);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else{
            log.error("Cannot search in VK: vkAuthInfo is null");
        }

        return url;
    }
}
