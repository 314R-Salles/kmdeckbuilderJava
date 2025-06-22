package fr.psalles.kmdeckbuilder.models.responses;

import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeChannelResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeSearchResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeVideoResponse;
import lombok.Data;

import java.math.BigInteger;
import java.time.Duration;

@Data
public class YoutubeSearchResultDto {

    private String id;
    private String url;
    private String title;
    private String thumbnailUrl;
    private String channelTitle;

    private String publishedAt;
    private BigInteger viewCount;
    private String duration;


    private String profileImage;


    public YoutubeSearchResultDto(YoutubeSearchResponse.SearchResult originalSearchResult, YoutubeChannelResponse.Channel channel, YoutubeVideoResponse.Video video) {
        this.id = originalSearchResult.getId().getVideoId();
        this.url = "https://www.youtube.com/watch?v=" + originalSearchResult.getId().getVideoId();
        this.channelTitle = originalSearchResult.getSnippet().getChannelTitle();
        this.title = originalSearchResult.getSnippet().getTitle();
        this.thumbnailUrl = originalSearchResult.getSnippet().getThumbnails().get("medium").getUrl();
        this.profileImage = channel.getSnippet().getThumbnails().get("medium").getUrl();


        this.publishedAt = originalSearchResult.getSnippet().getPublishedAt();
        this.viewCount = video.getStatistics().getViewCount();
        Duration duration = Duration.parse(video.getContentDetails().getDuration());
            this.duration = String.format("%d:%02d:%02d", duration.toHours(),
                duration.toMinutesPart(), duration.toSecondsPart());

    }
}
