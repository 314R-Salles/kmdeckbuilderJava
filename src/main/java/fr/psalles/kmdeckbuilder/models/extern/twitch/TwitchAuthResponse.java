package fr.psalles.kmdeckbuilder.models.extern.twitch;

import lombok.Data;

@Data
public class TwitchAuthResponse {
    private String access_token;
    private Long expires_in;
    private String token_type;
}
