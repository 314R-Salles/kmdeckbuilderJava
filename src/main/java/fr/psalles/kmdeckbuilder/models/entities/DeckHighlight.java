package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "km_deck_highlight")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeckHighlight {

    @EmbeddedId
    private AssociationIdentity id;

    @Column
    private Integer highlightOrder;

}
