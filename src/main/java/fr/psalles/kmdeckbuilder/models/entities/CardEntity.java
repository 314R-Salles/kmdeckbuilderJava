package fr.psalles.kmdeckbuilder.models.entities;

import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.CardType;
import fr.psalles.kmdeckbuilder.models.enums.God;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "km_card")
public class CardEntity {
    @EmbeddedId
    private Identity cardIdentity;
    @Column(name = "cardType")
    private CardType cardType;
    @Column(name = "life")
    private Integer life;
    @Column(name = "attack")
    private Integer attack;
    @Column(name = "movementPoint")
    private Integer movementPoint;
    @Column(name = "family1")
    private Integer family1;
    @Column(name = "family2")
    private Integer family2;
    @Column(name = "extensionId")
    private String extensionId;
    @Column(name = "rarity")
    private CardRarity rarity;
    @Column(name = "costAP")
    private Integer costAP;
    @Column(name = "godType")
    private God godType;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "cardFilePath")
    private String cardFilePath;
    @Column(name = "miniFilePath")
    private String miniFilePath;
    @Column(name = "infiniteName")
    private String infiniteName;

}
