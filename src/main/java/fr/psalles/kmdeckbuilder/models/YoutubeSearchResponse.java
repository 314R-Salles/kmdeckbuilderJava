package fr.psalles.kmdeckbuilder.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeSearchResponse {

    List<SearchResult> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResult {

        private VideoId id;
        private Snippet snippet;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class VideoId {
            private String videoId;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Snippet {
            private String channelId;
            private String channelTitle;
            private String title;
            private Map<String, Thumbnail> thumbnails;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Thumbnail {
                private String url;
            }
        }
    }
}
