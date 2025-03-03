package fr.psalles.kmdeckbuilder.models.requests;

import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.CardType;
import fr.psalles.kmdeckbuilder.models.enums.God;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardSearchForm {

    private CardType type;
    private Integer hpGreaterThan;
    private Integer hpLessThan;
    private Integer apGreaterThan;
    private Integer apLessThan;
    private Integer mpGreaterThan;
    private Integer mpLessThan;
    private Integer atGreaterThan;
    private Integer atLessThan;
    private List<God> gods;
    private CardRarity rarity;
    private String language;
    private Integer family;
    private String description;
    private String name;
    private Integer pageNumber;
    private Integer pageSize;

    @Override
    public String toString() {
        return "CardSearchForm{" +
                "type=" + type +
                ", hpGreaterThan=" + hpGreaterThan +
                ", hpLessThan=" + hpLessThan +
                ", apGreaterThan=" + apGreaterThan +
                ", apLessThan=" + apLessThan +
                ", mpGreaterThan=" + mpGreaterThan +
                ", mpLessThan=" + mpLessThan +
                ", atGreaterThan=" + atGreaterThan +
                ", atLessThan=" + atLessThan +
                ", god=" + gods +
                ", rarity=" + rarity +
                ", family=" + family +
                ", language='" + language + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
