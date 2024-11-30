package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.clients.YoutubeClient;
import fr.psalles.kmdeckbuilder.models.responses.YoutubeSearchResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class YoutubeService {

    final YoutubeClient youtubeClient;

    public YoutubeService(YoutubeClient youtubeClient) {
        this.youtubeClient = youtubeClient;
    }

    public List<YoutubeSearchResultDto> getVideos() {
        return youtubeClient.getLastVideos();
    }

    // Refresh toutes les 6h, à affiner selon les quotas
    // Quota dit : 100 requetes par jour, donc 1 toutes les 15 minutes ça passe.
//    @Scheduled(fixedRate = 15 * 60 * 1000)
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void evictStreams() {
        youtubeClient.evictVideos();
    }

}
