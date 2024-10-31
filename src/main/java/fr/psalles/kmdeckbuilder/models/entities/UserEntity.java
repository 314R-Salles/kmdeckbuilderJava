package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "km_user")
public class UserEntity {

    @Id
    @Column
    private String id;

    @Column
    private String username;

    @Column
    private boolean verified;

    @Column
    private String twitchUsername;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private int iconId;
}


