package fr.psalles.kmdeckbuilder.models.entities.specification;


import fr.psalles.kmdeckbuilder.models.entities.*;
import fr.psalles.kmdeckbuilder.models.enums.God;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class DeckSpecification {

    public static Specification<DeckEntity> filterByAp(Integer costAp, boolean geq) {
        return (root, query, builder) -> {
            if (costAp == null) {
                return builder.and();
            } else {
                if (geq) {
                    return builder.greaterThanOrEqualTo(root.get(DeckEntity_.costAP), costAp);
                } else {
                    return builder.lessThanOrEqualTo(root.get(DeckEntity_.costAP), costAp);
                }
            }
        };
    }


    public static Specification<DeckEntity> filterByDust(Integer dust, boolean geq) {
        return (root, query, builder) -> {
            if (dust == null) {
                return builder.and();
            } else {
                if (geq) {
                    return builder.greaterThanOrEqualTo(root.get(DeckEntity_.costDust), dust);
                } else {
                    return builder.lessThanOrEqualTo(root.get(DeckEntity_.costDust), dust);
                }
            }
        };
    }

    public static Specification<DeckEntity> filterByNameLikeContent(String name) {
        return (root, query, builder) -> {
            if (name == null) {
                return builder.and();
            } else {
                return builder.like(builder.lower(root.get(DeckEntity_.name)), "%" + name.toLowerCase() + "%");
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

    public static CriteriaBuilder.In<Long> filterByCard(Root<DeckEntity> root, CriteriaQuery query, CriteriaBuilder builder, Integer cardId) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<CardAssociation> cardAssociation = subquery.from(CardAssociation.class);
        subquery.select(cardAssociation.get(CardAssociation_.id).get(AssociationIdentity_.deckId))
                .where(cardAssociation.get(CardAssociation_.id).get(AssociationIdentity_.cardId).in(cardId));
        return builder.in(root.get(DeckEntity_.deckId)).value(subquery);
    }

    public static Specification<DeckEntity> filterByCards(List<Integer> cardIds) {
        return (root, query, builder) -> {
            if (cardIds == null) {
                return builder.and();
            } else {
                Predicate[] predicates = new Predicate[cardIds.size()];
                int i = 0;
                for (Integer cardId : cardIds) {
                    predicates[i++] = builder.and(filterByCard(root, query, builder, cardId));
                }
                return builder.and(predicates);
            }
        };
    }

    public static Specification<DeckEntity> filterByOwners(List<String> usernames) {
        return (root, query, builder) -> {
            if (usernames == null || usernames.isEmpty()) {
                return builder.and();
            } else {
                CriteriaBuilder.In<String> inClause = builder.in(root.get(DeckEntity_.userId).get(UserEntity_.username));
                for (String user : usernames) {
                    inClause.value(user);
                }
                return builder.and(inClause);
            }
        };
    }

}
