package fr.psalles.kmdeckbuilder.models;

import lombok.Data;

@Data
public class SearchResultDto {

    private String id;
    private String url;
    private String title;
    private String thumbnailUrl;
    private String channelTitle;

    private String profileImage;


    public SearchResultDto(YoutubeSearchResponse.SearchResult originalSearchResult, YoutubeChannelResponse.Channel channel) {
        this.id = originalSearchResult.getId().getVideoId();
        this.url = "https://www.youtube.com/watch?v=" + originalSearchResult.getId().getVideoId();
        this.channelTitle = originalSearchResult.getSnippet().getChannelTitle();
        this.title = originalSearchResult.getSnippet().getTitle();
        this.thumbnailUrl = originalSearchResult.getSnippet().getThumbnails().get("medium").getUrl();
        this.profileImage = channel.getSnippet().getThumbnails().get("medium").getUrl();
    }
}
