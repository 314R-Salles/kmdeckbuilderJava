package fr.psalles.kmdeckbuilder.models.extern.twitch;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchVideoResponse {

    private List<Video> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Video {

        @JsonAlias(value = "id")
        private String id; // besoin d'un id unique pour le track du forEach angular
        @JsonAlias(value = "user_login")
        private String username;
        @JsonAlias(value = "user_name")
        private String displayName;
        @JsonAlias(value = "title")
        private String title;
        @JsonAlias(value = "created_at")
        private String created_at;
        @JsonAlias(value = "url")
        private String url;
        @JsonAlias(value = "thumbnail_url")
        private String thumbnailUrl;
        @JsonAlias(value = "view_count")
        private BigDecimal viewCount;
        @JsonAlias(value = "language")
        private String language;
        @JsonAlias(value = "type")
        private String type;
        @JsonAlias(value = "duration")
        private String duration;

        public List<String> tags;

    }
}
