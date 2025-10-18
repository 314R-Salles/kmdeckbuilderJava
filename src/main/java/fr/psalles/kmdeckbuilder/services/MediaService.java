package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.clients.TwitchClient;
import fr.psalles.kmdeckbuilder.clients.YoutubeClient;
import fr.psalles.kmdeckbuilder.commons.exceptions.UnauthorizedException;
import fr.psalles.kmdeckbuilder.models.VideoCheck;
import fr.psalles.kmdeckbuilder.models.responses.Media;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class MediaService {

    private final TwitchClient twitchClient;
    private final YoutubeClient youtubeClient;

    public MediaService(
            TwitchClient twitchClient, YoutubeClient youtubeClient
    ) {
        this.twitchClient = twitchClient;
        this.youtubeClient = youtubeClient;
    }

    public String getUsernameFromToken(String token) {
        return twitchClient.getUserInfoFromAuthToken(token).getDisplayName();
    }

    public List<Media> getStreams() {
        String token = twitchClient.getBearerToken();
        try {
            return twitchClient.getStreams(token);
        } catch (UnauthorizedException e) {
            // on suppose que c'est le token qui a expiré
            twitchClient.evictToken();
            twitchClient.getBearerToken();
            return twitchClient.getStreams(token);
        }
    }

    public List<Media> getVods() {
        String token = twitchClient.getBearerToken();
        try {
            return twitchClient.getVods(token);
        } catch (UnauthorizedException e) {
            // on suppose que c'est le token qui a expiré
            twitchClient.evictToken();
            twitchClient.getBearerToken();
            return twitchClient.getVods(token);
        }
    }

    public List<Media> getVodsAndVideos() {
        return Stream.concat(this.getVods().stream(), this.getVideos().stream()).toList();
    }

    public VideoCheck checkVideo(String id) {
        String token = twitchClient.getBearerToken();
        try {
            return twitchClient.checkVideo(token, id);
        } catch (UnauthorizedException e) {
            // on suppose que c'est le token qui a expiré
            twitchClient.evictToken();
            twitchClient.getBearerToken();
            return twitchClient.checkVideo(token, id);
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void evictStreams() {
        twitchClient.evictStreams();
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void evictVods() {
        twitchClient.evictVods();
    }

    public List<Media> getVideos() {
        return youtubeClient.getLastVideos();
    }

    // Refresh toutes les 6h, à affiner selon les quotas
    // Quota dit : 100 requetes par jour, donc 1 toutes les 15 minutes ça passe.
    // @Scheduled(fixedRate = 15 * 60 * 1000)
    @Scheduled(fixedRate = 60 * 60 * 1000 * 10)
    public void evictYoutube() {
        youtubeClient.evictVideos();
    }
}
