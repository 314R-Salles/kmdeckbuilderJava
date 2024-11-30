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

    private String name;
    private List<God> gods;
    private Integer costAP;


}
