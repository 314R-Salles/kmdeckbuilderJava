package fr.psalles.kmdeckbuilder.models.entities;

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
public class Identity {

    @Column
    private int id;

    @Column
    @Enumerated(EnumType.STRING)
    private Language language;


}
