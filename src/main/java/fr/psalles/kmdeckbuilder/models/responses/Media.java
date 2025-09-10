package fr.psalles.kmdeckbuilder.models.responses;

import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchStreamResponse;
import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchStreamerResponse;
import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchVideoResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeChannelResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeSearchResponse;
import fr.psalles.kmdeckbuilder.models.extern.youtube.YoutubeVideoResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;

@Data
public class Media {
    private String displayName;
    private String title;
    private String created_at;
    private String url;
    private String thumbnailUrl;
    private BigDecimal view;
    private String duration;
    private String profileImage;
    private boolean youtube;
    private boolean live;

    public Media(TwitchVideoResponse.Video video, TwitchStreamerResponse.Streamer streamer) {
        this.displayName = video.getDisplayName();
        this.title = video.getTitle();
        this.created_at = video.getCreated_at();
        this.url = video.getUrl();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.view = video.getViewCount();
        this.duration = video.getDuration();
        this.profileImage = streamer.getProfileImage();
        this.youtube = false;
        this.live = false;
    }

    public Media(TwitchStreamResponse.Stream stream, TwitchStreamerResponse.Streamer streamer){
        this.displayName = stream.displayName;
        this.title = stream.title;
        this.created_at = stream.startedAt;
        this.url =  "https://www.twitch.tv/" + stream.displayName.toLowerCase();
        this.thumbnailUrl = stream.thumbnailUrl;
        this.view = stream.viewerCount;
        this.profileImage = streamer.getProfileImage();
        this.youtube = false;
        this.live = true;
    }

    public Media(YoutubeSearchResponse.SearchResult originalSearchResult, YoutubeChannelResponse.Channel channel, YoutubeVideoResponse.Video video) {
        this.displayName = originalSearchResult.getSnippet().getChannelTitle();
        this.title = originalSearchResult.getSnippet().getTitle();
        this.created_at = originalSearchResult.getSnippet().getPublishedAt();
        this.url = "https://www.youtube.com/watch?v=" + originalSearchResult.getId().getVideoId();
        this.thumbnailUrl = originalSearchResult.getSnippet().getThumbnails().get("medium").getUrl();
        this.view = new BigDecimal(video.getStatistics().getViewCount());
        Duration duration = Duration.parse(video.getContentDetails().getDuration());
        this.duration = String.format("%dh%02dm%02ds", duration.toHours(),
                duration.toMinutesPart(), duration.toSecondsPart());
        this.profileImage = channel.getSnippet().getThumbnails().get("medium").getUrl();
        this.youtube = true;
        this.live = "live".equals(originalSearchResult.getSnippet().getLiveBroadcastContent());
    }
}
