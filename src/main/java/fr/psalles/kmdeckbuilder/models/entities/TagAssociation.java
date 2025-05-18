package fr.psalles.kmdeckbuilder.models.entities;

import fr.psalles.kmdeckbuilder.models.entities.embedded.TagAssociationIdentity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "km_tag_association")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagAssociation {

    @EmbeddedId
    private TagAssociationIdentity id;

}
