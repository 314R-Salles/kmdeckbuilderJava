package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "km_card_illustrations")
public class IllustrationNameEntity {
    @Id
    @Column
    private int id;

    @Column
    private String cardName;

}
