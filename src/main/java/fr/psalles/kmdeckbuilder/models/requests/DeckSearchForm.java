package fr.psalles.kmdeckbuilder.models.requests;

import fr.psalles.kmdeckbuilder.models.enums.God;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckSearchForm {

    private String content;
    private List<God> gods;
    private List<Integer> cards;
    private List<Integer> tags;
    private List<Integer> negativeTags;
    private List<String> users;
    private List<String> negativeUsers;
    private Integer actionPointCost;
    private Integer dustCost;
    private boolean dustGeq;
    private boolean actionCostGeq;

    private Integer page;
    private Integer pageSize;

    private boolean favoritesOnly;
    private Language language;

}
