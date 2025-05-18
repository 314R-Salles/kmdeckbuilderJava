package fr.psalles.kmdeckbuilder.models.entities;

import fr.psalles.kmdeckbuilder.models.entities.embedded.DeckIdentity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "km_last_version")
public class LastVersionEntity {
    @EmbeddedId
    private DeckIdentity id;

}
