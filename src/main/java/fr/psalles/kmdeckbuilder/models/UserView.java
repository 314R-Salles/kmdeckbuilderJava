package fr.psalles.kmdeckbuilder.models;

import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserView {

    String username;
    String twitchUsername;
    String lastLogin;

    public UserView(UserEntity entity) {
        this.username = entity.getUsername();
        this.twitchUsername = entity.getTwitchUsername();
        this.lastLogin = entity.getLastLogin().toString();
    }

}
