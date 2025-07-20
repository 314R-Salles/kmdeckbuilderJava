package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.clients.TwitchClient;
import fr.psalles.kmdeckbuilder.commons.exceptions.UnauthorizedException;
import fr.psalles.kmdeckbuilder.models.VideoCheck;
import fr.psalles.kmdeckbuilder.models.responses.AggregatedStream;
import fr.psalles.kmdeckbuilder.models.responses.AggregatedVod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TwitchService {

    final TwitchClient twitchClient;

    public TwitchService(
            TwitchClient twitchClient
    ) {
        this.twitchClient = twitchClient;
    }

    public String getUsernameFromToken(String token) {
        return twitchClient.getUserInfoFromAuthToken(token).getDisplayName();
    }

    public List<AggregatedStream> getStreams() {
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

    public List<AggregatedVod> getVods() {
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

}
