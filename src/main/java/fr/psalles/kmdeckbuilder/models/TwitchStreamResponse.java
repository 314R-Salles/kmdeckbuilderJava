package fr.psalles.kmdeckbuilder.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchStreamResponse {

    List<Stream> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stream {

        @JsonAlias(value = "user_login")
        public String username;

        @JsonAlias(value = "user_name")
        public String displayName;

        @JsonAlias(value = "title")
        public String title;

        @JsonAlias(value = "viewer_count")
        public BigDecimal viewerCount;

        @JsonAlias(value = "started_at")
        public String startedAt;

        @JsonAlias(value = "language")
        public String language;

        @JsonAlias(value = "thumbnail_url")
        public String thumbnailUrl;

        @JsonAlias(value = "tags")
        public List<String> tags;

    }
}
