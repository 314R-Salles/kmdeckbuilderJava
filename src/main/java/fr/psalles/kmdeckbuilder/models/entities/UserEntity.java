package fr.psalles.kmdeckbuilder.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "km_user")
public class UserEntity {

    @Id
    @Column
    private String userId;

    @Column
    private String username;

    @Column
    private boolean verified;

    @Column
    private LocalDateTime lastValidationEmail;

    @Column
    private String twitchUsername;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private Integer iconId;

    @Column
    private boolean admin;

    @Column
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "id.userId" // a.b pour embeddedId
    )
    @JsonIgnore
    private List<FavoriteAssociation> favorites = new ArrayList<>();

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", verified=" + verified +
                ", twitchUsername='" + twitchUsername + '\'' +
                '}';
    }
}


