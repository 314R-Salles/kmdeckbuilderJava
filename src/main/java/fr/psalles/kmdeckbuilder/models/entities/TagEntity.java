package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.Column;
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
@Table(name = "km_tags")
public class TagEntity {
    @EmbeddedId
    private Identity tagIdentity;

    @Column
    private String name;

    @Column
    private String iconId;

    @Column
    private boolean disabled;

}
