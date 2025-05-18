package fr.psalles.kmdeckbuilder.models.entities.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AssociationIdentity {

    @Embedded
    private DeckIdentity id;

    @Column
    private int cardId;

}
