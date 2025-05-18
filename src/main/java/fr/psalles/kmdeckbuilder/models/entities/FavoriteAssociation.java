package fr.psalles.kmdeckbuilder.models.entities;

import fr.psalles.kmdeckbuilder.models.entities.embedded.FavoriteAssociationIdentity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "km_favorite_association")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteAssociation {

    @EmbeddedId
    private FavoriteAssociationIdentity id;
    @Column
    private LocalDateTime favoriteDate;

}
