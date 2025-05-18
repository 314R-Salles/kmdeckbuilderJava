package fr.psalles.kmdeckbuilder.models.entities;

import fr.psalles.kmdeckbuilder.models.entities.embedded.AssociationIdentity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "km_association")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardAssociation {

    @EmbeddedId
    private AssociationIdentity id;

    @Column
    private int nbrExemplaires;

}
