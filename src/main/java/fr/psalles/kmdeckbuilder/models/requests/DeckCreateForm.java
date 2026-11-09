package fr.psalles.kmdeckbuilder.models.requests;

import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.God;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckCreateForm {

    @NotNull
    private List<Card> cards;

    @NotBlank
    @Size(max = 200)
    private String name;
    private String description;

    @Size(max = 255)
    private String videoLink;
    private God god;
    @NotNull
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
