package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AssociationIdentity {

    @Column
    private Long deckId;

    @Column
    private int cardId;

}
