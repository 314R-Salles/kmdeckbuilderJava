package fr.psalles.kmdeckbuilder.models;

import fr.psalles.kmdeckbuilder.models.enums.God;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeckDto {

    private String deckId;
    private String name;
    private String owner;
    private boolean owned;
    private String description;
    private List<CardDto> cards;
    private God god;
    private LocalDateTime creationDate;
    private int costAP;
    private int costDust;
    private List<HighlightDto> highlights;

    private int version;
    private List<Integer> versions;
    private List<SimpleTagDto> tags;

    private boolean liked;
    private int favoriteCount;



}
