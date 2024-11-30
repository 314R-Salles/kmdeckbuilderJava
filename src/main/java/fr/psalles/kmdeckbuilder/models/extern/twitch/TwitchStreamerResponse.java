package fr.psalles.kmdeckbuilder.models.extern.twitch;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchStreamerResponse {

    private List<Streamer> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Streamer {

        //        @JsonAlias(value = "id")
//        private  String userId;
//
        @JsonAlias(value = "login")
        private String username;
        //
//        @JsonAlias(value = "display_name")
//        private  String displayName;
//
//        private  String description;
//
        @JsonAlias(value = "profile_image_url")
        private String profileImage;

//        @JsonAlias(value = "offline_image_url")
//        private String offlineImage;

//        @JsonAlias(value = "view_count")
//        private  BigDecimal viewCount;

    }

}
