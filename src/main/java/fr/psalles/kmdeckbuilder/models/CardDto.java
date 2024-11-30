package fr.psalles.kmdeckbuilder.models;


import fr.psalles.kmdeckbuilder.models.entities.Identity;
import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.CardType;
import fr.psalles.kmdeckbuilder.models.enums.God;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private int id;
    private CardType cardType;

    private CardRarity rarity;
    private Integer costAP;
    private God godType;
    private String name;
    private String cardFilePath;
    private String miniFilePath;
    private String infiniteName;

    // pour plus tard, la recherche rapide / recherche mobile
    private Integer life;
    private Integer attack;
    private Integer movementPoint;


    // a la lecture
    private Integer count;
}
