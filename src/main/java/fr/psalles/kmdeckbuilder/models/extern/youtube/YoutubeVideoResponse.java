package fr.psalles.kmdeckbuilder.models.extern.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeVideoResponse {

    private List<Video> items;
//    String nextPageToken;


//    https://developers.google.com/youtube/v3/docs/videos#properties
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Video {

        private VideoContentDetails contentDetails;
        private String id;
        //        private Map<String, VideoLocalization> localizations; // peut etre plus tard
//        private VideoPlayer player;
//        private VideoSnippet snippet;
        private VideoStatistics statistics;
//        private VideoStatus status;
//        private VideoTopicDetails topicDetails;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class VideoContentDetails {
            private String duration;
//            private String caption;
//            private String definition;
//            private String dimension;
//            private Boolean licensedContent;
//            private String projection;
//            private Boolean hasCustomThumbnail;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class VideoSnippet {
//            private String categoryId;
            private String channelId;
            private String channelTitle;
//            private String defaultAudioLanguage;
//            private String defaultLanguage;
            private String description;
//            private String liveBroadcastContent;
            private String publishedAt;
//            private LocalDateTime publishedTime;
//            private List<String> tags;
//            private Map<String, Thumbnail> thumbnails;
            private String title;

        }


        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class VideoStatistics {
            private BigInteger viewCount;
//            private BigInteger commentCount;
            private BigInteger likeCount;
        }


    }
}

