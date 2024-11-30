package fr.psalles.kmdeckbuilder.models.responses;

import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeChannelResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeSearchResponse;
import lombok.Data;

@Data
public class YoutubeSearchResultDto {

    private String id;
    private String url;
    private String title;
    private String thumbnailUrl;
    private String channelTitle;

    private String profileImage;


    public YoutubeSearchResultDto(YoutubeSearchResponse.SearchResult originalSearchResult, YoutubeChannelResponse.Channel channel) {
        this.id = originalSearchResult.getId().getVideoId();
        this.url = "https://www.youtube.com/watch?v=" + originalSearchResult.getId().getVideoId();
        this.channelTitle = originalSearchResult.getSnippet().getChannelTitle();
        this.title = originalSearchResult.getSnippet().getTitle();
        this.thumbnailUrl = originalSearchResult.getSnippet().getThumbnails().get("medium").getUrl();
        this.profileImage = channel.getSnippet().getThumbnails().get("medium").getUrl();
    }
}
