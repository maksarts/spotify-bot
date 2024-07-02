package ru.maksarts.spotifybot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VkService {

    protected static Pattern patternAudioUrl;

    static {
        patternAudioUrl = Pattern.compile("(https://)");
    }

    public String getAudioUrl(String artist, String songName) {
        String url = null;
        try {

            ProcessBuilder processBuilder = new ProcessBuilder("python",
                                                                        "pyscripts/vk_audio_script.py",
                                                                        "--query", artist + " - " + songName);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            bufferedWriter.write("OK");
            bufferedWriter.flush();
            bufferedWriter.close();

            String line = bufferedReader.readLine();
            while (line != null) {

                Matcher matcher = patternAudioUrl.matcher(line);
                if(matcher.find()){
                    url = line;
                }

                log.info(line);
                line = bufferedReader.readLine();
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

        return url;
    }
}
