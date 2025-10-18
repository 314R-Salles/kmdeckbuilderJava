package fr.psalles.kmdeckbuilder.models.extern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    private String access_token;
    private Long expires_in;
    private String token_type;
    private String scope;
}
