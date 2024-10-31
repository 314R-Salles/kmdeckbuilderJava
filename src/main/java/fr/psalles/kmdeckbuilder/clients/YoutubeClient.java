package fr.psalles.kmdeckbuilder.clients;

import fr.psalles.kmdeckbuilder.commons.client.BaseHttpClient;
import fr.psalles.kmdeckbuilder.models.SearchResultDto;
import fr.psalles.kmdeckbuilder.models.YoutubeChannelResponse;
import fr.psalles.kmdeckbuilder.models.YoutubeSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    BaseHttpClient client;

    String key = "nope";

    @Cacheable("youtube_videos")
    public List<SearchResultDto> getLastVideos() {
        log.info("Api call : Youtube Search");
        String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=20&order=date&q=krosmaga&key="+key;

        List<YoutubeSearchResponse.SearchResult> searchResults = client.makeCall(HttpMethod.GET, url, YoutubeSearchResponse.class, null, null).getItems();
        List<String> channelIds = searchResults.stream().map(a -> a.getSnippet().getChannelId()).distinct().collect(Collectors.toList());
        List<YoutubeChannelResponse.Channel> channels = getChannels(channelIds);
        Map<String, YoutubeChannelResponse.Channel> channelsMap = channels.stream()
                .collect(Collectors.toMap(YoutubeChannelResponse.Channel::getId, Function.identity(), (a, b) -> a));

        return searchResults.stream().map(searchResult -> new SearchResultDto(searchResult, channelsMap.get(searchResult.getSnippet().getChannelId()))).collect(Collectors.toList());
    }

    private List<YoutubeChannelResponse.Channel> getChannels(List<String> channelIds) {
        int maxResults = channelIds.size();
        String params = String.join(",", channelIds); // pas besoin d'encoder le , c'est fait tout seul lors de l'appel.
        log.info("Api call : youtube channel");
        String url = "https://youtube.googleapis.com/youtube/v3/channels?part=snippet&id=" + params + "&maxResults=" + maxResults + "&key="+key;
        return client.makeCall(HttpMethod.GET, url, YoutubeChannelResponse.class, null, null).getItems();
    }


    @CacheEvict("youtube_videos")
    public void evictVideos() {
    }

}
