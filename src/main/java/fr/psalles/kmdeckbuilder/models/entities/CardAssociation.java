package fr.psalles.kmdeckbuilder.models.entities;

import fr.psalles.kmdeckbuilder.models.entities.embedded.AssociationIdentity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CardAssociation that = (CardAssociation) o;
        return nbrExemplaires == that.nbrExemplaires && id.getCardId() == that.id.getCardId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nbrExemplaires);
    }
}
