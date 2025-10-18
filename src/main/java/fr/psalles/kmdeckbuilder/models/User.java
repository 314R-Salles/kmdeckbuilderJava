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
public class User {

    String username;
    String twitchUsername;
    String lastLogin;
    String lastEmail;
    Integer iconId;
    boolean verified;
    boolean isAdmin; // attention la blague de Lombok qui transforme ça en "admin" puisqu'il voit un champ = getter

    public User(UserEntity entity) {
        this.username = entity.getUsername();
        this.twitchUsername = entity.getTwitchUsername();
        this.lastLogin = entity.getLastLogin().toString();
        this.iconId = entity.getIconId() != null ? entity.getIconId() : 0;
        this.isAdmin = entity.isAdmin();
        this.verified = entity.isVerified();
        this.lastEmail = entity.getLastValidationEmail() != null ? entity.getLastValidationEmail().toString() : null;

    }

}
