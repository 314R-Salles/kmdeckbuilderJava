package fr.psalles.kmdeckbuilder.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    String username;
    String twitchUsername;
    String lastLogin;
    String lastEmail;
    String passwordChange;
    Integer iconId;
    Boolean verified;
    Boolean admin;

    public User(UserEntity entity) {
        this.username = entity.getUsername();
        this.twitchUsername = entity.getTwitchUsername();
        this.lastLogin = entity.getLastLogin().toString();
//        this.iconId = entity.getIconId() != null ? entity.getIconId() : 0;
        this.admin = entity.isAdmin();
        this.verified = entity.isVerified();
        this.lastEmail = entity.getLastValidationEmail() != null ? entity.getLastValidationEmail().toString() : null;
        this.passwordChange = entity.getLastPasswordChangeRequest() != null ? entity.getLastPasswordChangeRequest().toString() : null;
    }

}
