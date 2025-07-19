package fr.psalles.kmdeckbuilder.models.requests;

import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.God;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckCreateForm {

    private List<Card> cards;
    private String name;
    private String description;
    private String videoLink;
    private God god;
    private List<Integer> tags;

    private String deckId;


    @Data
    public static class Card {

       private int id;
       private int count;
       private int costAP;
       private CardRarity rarity;
       private Integer highlight;


    }
}
