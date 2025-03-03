package fr.psalles.kmdeckbuilder.models.requests;

import fr.psalles.kmdeckbuilder.models.enums.God;
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
    private List<String> users;
    private Integer actionPointCost;
    private Integer dustCost;
    private Boolean dustGeq;
    private Boolean actionCostGeq;

}
