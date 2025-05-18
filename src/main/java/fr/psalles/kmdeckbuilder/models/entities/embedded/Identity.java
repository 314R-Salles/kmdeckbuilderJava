package fr.psalles.kmdeckbuilder.models.entities.embedded;

import fr.psalles.kmdeckbuilder.models.enums.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
// Un id composé pour les tables qui ont des traductions
public class Identity {

    @Column
    private int id;

    @Column
    @Enumerated(EnumType.STRING)
    private Language language;


}
