package fr.psalles.kmdeckbuilder.models.extern.auth0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends PaginatedResponse {

    private List<User> users;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        String user_id;
        boolean email_verified;
    }
}
