package fr.psalles.kmdeckbuilder.models.responses;

import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchStreamResponse;
import fr.psalles.kmdeckbuilder.models.extern.twitch.TwitchStreamerResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AggregatedStream {
    private String username;
    private String displayName;
    private String title;
    private BigDecimal viewerCount;
    private String startedAt;
    private String language;
    private String thumbnailUrl;
    private List<String> tags;
    private String profileImage;

    public AggregatedStream(TwitchStreamResponse.Stream stream, TwitchStreamerResponse.Streamer streamer){
        this.username = stream.username;
        this.displayName = stream.displayName;
        this.title = stream.title;
        this.viewerCount = stream.viewerCount;
        this.startedAt = stream.startedAt;
        this.language = stream.language;
        this.thumbnailUrl = stream.thumbnailUrl;
        this.tags = stream.tags;
        this.profileImage = streamer.getProfileImage();
    }
}
