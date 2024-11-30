package fr.psalles.kmdeckbuilder.models.extern.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeChannelResponse {

    private List<Channel> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Channel {

        private String id;
        private Snippet snippet;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Snippet {
            private Map<String, Thumbnail> thumbnails;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Thumbnail {
                private String url;
            }
        }
    }
}
