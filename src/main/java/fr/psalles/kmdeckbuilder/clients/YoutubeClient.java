package fr.psalles.kmdeckbuilder.clients;

import fr.psalles.kmdeckbuilder.commons.client.BaseHttpClient;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeVideoResponse;
import fr.psalles.kmdeckbuilder.models.responses.Media;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeChannelResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YoutubeClient {

    @Autowired
    private BaseHttpClient client;

    @Value("${youtube.key}")
    private String key;

    @Cacheable("youtube_videos")
    public List<Media> getLastVideos() {
        log.debug("Api call : Youtube Search");
        String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=15&order=date&q=krosmaga&key=" + key;

        List<YoutubeSearchResponse.SearchResult> searchResults = client.makeCall(HttpMethod.GET, url, YoutubeSearchResponse.class, null, null).getItems();
        // On ne veut pas afficher les vidéos " A VENIR"
        searchResults = searchResults.stream().filter(result -> !result.getSnippet().getLiveBroadcastContent().equals("upcoming")).toList();
        List<String> channelIds = searchResults.stream().map(a -> a.getSnippet().getChannelId()).distinct().toList();
        List<YoutubeChannelResponse.Channel> channels = getChannels(channelIds);
        Map<String, YoutubeChannelResponse.Channel> channelsMap = channels.stream()
                .collect(Collectors.toMap(YoutubeChannelResponse.Channel::getId, Function.identity(), (a, b) -> a));


        List<String> videoIds = searchResults.stream().map(video -> video.getId().getVideoId()).toList();
        List<YoutubeVideoResponse.Video> videos = getVideos(videoIds);
        Map<String, YoutubeVideoResponse.Video> videossMap = videos.stream()
                .collect(Collectors.toMap(YoutubeVideoResponse.Video::getId, Function.identity(), (a, b) -> a));

        return searchResults.stream().map(searchResult ->
                new Media(searchResult,
                        channelsMap.get(searchResult.getSnippet().getChannelId()),
                        videossMap.get(searchResult.getId().getVideoId()))
                        )
               .toList();
    }

    private List<YoutubeChannelResponse.Channel> getChannels(List<String> channelIds) {
        int maxResults = channelIds.size();
        String params = String.join(",", channelIds);
        log.debug("Api call : youtube channel");
        String url = "https://youtube.googleapis.com/youtube/v3/channels?part=snippet&id=" + params + "&maxResults=" + maxResults + "&key=" + key;
        return client.makeCall(HttpMethod.GET, url, YoutubeChannelResponse.class, null, null).getItems();
    }

    private List<YoutubeVideoResponse.Video> getVideos(List<String> videoIds) {
        String params = String.join(",", videoIds); // pas besoin d'encoder le , c'est fait tout seul lors de l'appel.
        log.debug("Api call : youtube videos");
        String url = "https://youtube.googleapis.com/youtube/v3/videos?part=statistics,contentDetails&id=" + params + "&key=" + key;
        return client.makeCall(HttpMethod.GET, url, YoutubeVideoResponse.class, null, null).getItems();
    }



    @CacheEvict("youtube_videos")
    public void evictVideos() {
    }

}
