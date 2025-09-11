package fr.psalles.kmdeckbuilder.models.entities.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DeckIdentity {

    @Column
    private String deckId;

    @Column
    private int version;

}