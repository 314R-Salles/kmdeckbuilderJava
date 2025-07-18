package fr.psalles.kmdeckbuilder.clients;

import fr.psalles.kmdeckbuilder.commons.client.BaseHttpClient;
import fr.psalles.kmdeckbuilder.models.VideoCheck;
import fr.psalles.kmdeckbuilder.models.extern.twitch.*;
import fr.psalles.kmdeckbuilder.models.responses.AggregatedStream;
import fr.psalles.kmdeckbuilder.models.responses.AggregatedVod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TwitchClient {

    @Autowired
    BaseHttpClient client;

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    public static String getIdConcatenation(List<String> params) {
        return getFieldConcatenation("id", params);
    }

    public static String getUsernameConcatenation(List<String> params) {
        return getFieldConcatenation("login", params);
    }

    // Méthode private : Concaténation
    private static String getFieldConcatenation(String field, List<String> params) {
        StringBuilder result = new StringBuilder();
        for (String paramValue : params) {
            result.append(URLEncoder.encode(field, StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(paramValue, StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return !resultString.isEmpty()
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public TwitchUserResponse.User getUserInfoFromAuthToken(String token) {
        TwitchUserResponse response = client.makeCall(HttpMethod.GET, "https://api.twitch.tv/helix/users", TwitchUserResponse.class, null, getAuthHeaders(token));
        log.debug("Le token reçu correspond à : {} ", response.getData().getFirst().getLogin());
        return response.getData().getFirst();
    }

    // Valider qu'une vidéo est un highlight et donc un lien permanent
    public VideoCheck checkVideo(String token, String id) {
        String url = "https://api.twitch.tv/helix/videos?id=" + id; // League
        TwitchVideoResponse video = client.makeCall(HttpMethod.GET, url, TwitchVideoResponse.class, null, getAuthHeaders(token));
        return new VideoCheck("twitch", !video.getData().getFirst().getType().equals("highlight"), true, id);
    }

    @Cacheable("twitch_token")
    public String getBearerToken() {
        log.debug("Api call : Auth twitch");
        String url = "https://id.twitch.tv/oauth2/token";
        return client.makeCall(HttpMethod.POST, url, TwitchAuthResponse.class, "grant_type=client_credentials&client_secret=" +
                        clientSecret + "&client_id=" + clientId,
                getUrlEncodedHeaders()).getAccess_token();
    }

    // Gestion du cache

    @Cacheable("current_streams")
    public List<AggregatedStream> getStreams(String token) {
        log.debug("Api call : Streams Krosmaga");

        String url = "https://api.twitch.tv/helix/streams?game_id=493754&first=12"; // League
        TwitchStreamResponse streams = client.makeCall(HttpMethod.GET, url, TwitchStreamResponse.class, null, getAuthHeaders(token));

        // Completer les données avec l'image de profil
        if (streams.getData().isEmpty()) return Collections.emptyList();
        TwitchStreamerResponse streamers = getStreamers(token, streams.getData().stream().map(TwitchStreamResponse.Stream::getUsername).toList());
        Map<String, TwitchStreamerResponse.Streamer> streamerMap = streamers.getData().stream()
                .collect(Collectors.toMap(TwitchStreamerResponse.Streamer::getUsername, Function.identity(), (a, b) -> a));

        return streams.getData().stream().map(stream -> new AggregatedStream(stream, streamerMap.get(stream.getUsername()))).toList();
    }

    @Cacheable("vods")
    public List<AggregatedVod> getVods(String token) {
        log.debug("Api call : Vods Krosmaga");

        String url = "https://api.twitch.tv/helix/videos?game_id=493754&first=16&sort=time&type=archive"; // League
        TwitchVideoResponse vods = client.makeCall(HttpMethod.GET, url, TwitchVideoResponse.class, null, getAuthHeaders(token));

        // Completer les données avec l'image de profil
        if (vods.getData().isEmpty()) return Collections.emptyList();
        TwitchStreamerResponse streamers = getStreamers(token, vods.getData().stream().map(TwitchVideoResponse.Video::getUsername).distinct().toList());
        Map<String, TwitchStreamerResponse.Streamer> streamerMap = streamers.getData().stream()
                .collect(Collectors.toMap(TwitchStreamerResponse.Streamer::getUsername, Function.identity(), (a, b) -> a));

        return vods.getData().stream().map(stream -> new AggregatedVod(stream, streamerMap.get(stream.getUsername()))).toList();
    }

    private TwitchStreamerResponse getStreamers(String token, List<String> usernames) {
        log.debug("Api call : streamers");
        String url = "https://api.twitch.tv/helix/users?" + getUsernameConcatenation(usernames);
        return client.makeCall(HttpMethod.GET, url, TwitchStreamerResponse.class, null, getAuthHeaders(token));
    }

    @CacheEvict("twitch_token")
    public void evictToken() {
        // Vide car Cache Evict
    }

    // Besoin du allEntries, parce que je passe un param dans getStreams qui sert donc de "clé" pour le cache des streams  cache = (Map<token, streams)
    @CacheEvict(value = "current_streams", allEntries = true)
    public void evictStreams() {
        // Vide car Cache Evict
    }

    // Besoin du allEntries, parce que je passe un param dans getStreams qui sert donc de "clé" pour le cache des streams  cache = (Map<token, streams)
    @CacheEvict(value = "vods", allEntries = true)
    public void evictVods() {
        // Vide car Cache Evict
    }


    // Génération des headers pour les requêtes Twitch
    private HttpHeaders getUrlEncodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private HttpHeaders getAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Client-ID", clientId);
        return headers;
    }
}
