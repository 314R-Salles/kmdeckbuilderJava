package fr.psalles.kmdeckbuilder.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.God;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckForm {

    private List<Card> cards;
    private String name;
    private God god;

    @Data
    public static class Card {

       private String id;
       private int count;
       private int costAP;
       private CardRarity rarity;
       private int hightlight;


    }
}
