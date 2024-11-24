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
    private String twitchUsername;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private Integer iconId;

// ? donc non la relation a pas besoin d'etre "symetrique"???
//    @OneToMany(
//            fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL,
//            orphanRemoval = true,
//            mappedBy = "userId"
//    )
//    @JsonIgnore
//    private List<DeckEntity> decks = new ArrayList<>();

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


