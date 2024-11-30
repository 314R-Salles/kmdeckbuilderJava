package fr.psalles.kmdeckbuilder.models.responses;

import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchStreamerResponse;
import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchVideoResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AggregatedVod {
    private String id;
    private String username;
    private String displayName;
    private String title;
    private String created_at;
    private String url;
    private String thumbnailUrl;
    private BigDecimal viewCount;
    private String language;
    private String type;
    private String duration;
    private String profileImage;

    public AggregatedVod(TwitchVideoResponse.Video video, TwitchStreamerResponse.Streamer streamer) {
        this.id = video.getId();
        this.username = video.getUsername();
        this.displayName = video.getDisplayName();
        this.title = video.getTitle();
        this.created_at = video.getCreated_at();
        this.url = video.getUrl();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.viewCount = video.getViewCount();
        this.language = video.getLanguage();
        this.type = video.getType();
        this.duration = video.getDuration();
        this.profileImage = streamer.getProfileImage();
    }
}
