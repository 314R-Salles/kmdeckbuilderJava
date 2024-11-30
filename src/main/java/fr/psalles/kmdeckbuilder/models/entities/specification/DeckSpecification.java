package fr.psalles.kmdeckbuilder.models.entities.specification;


import fr.psalles.kmdeckbuilder.models.entities.DeckEntity;
import fr.psalles.kmdeckbuilder.models.entities.DeckEntity_;
import fr.psalles.kmdeckbuilder.models.enums.God;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class DeckSpecification {

    public static Specification<DeckEntity> filterByApGreaterThan(Integer apMin) {
        return (root, query, builder) -> {
            if (apMin == null) {
                return builder.and();
            } else {
                return builder.greaterThanOrEqualTo(root.get(DeckEntity_.costAP), apMin);
            }
        };
    }

    public static Specification<DeckEntity> filterByApLessThan(Integer apMax) {
        return (root, query, builder) -> {
            if (apMax == null) {
                return builder.and();
            } else {
                return builder.lessThanOrEqualTo(root.get(DeckEntity_.costAP), apMax);
            }
        };
    }


    public static Specification<DeckEntity> filterByGod(List<God> gods) {
        return (root, query, builder) -> {
            if (gods == null) {
                return builder.and();
            } else {
                CriteriaBuilder.In<God> inClause = builder.in(root.get(DeckEntity_.god));
                for (God god : gods) {
                    inClause.value(god);
                }
                return builder.and(inClause);
            }
        };
    }

}
