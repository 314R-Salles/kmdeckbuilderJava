package fr.psalles.kmdeckbuilder.models.entities.specification;


import fr.psalles.kmdeckbuilder.models.entities.CardEntity;
import fr.psalles.kmdeckbuilder.models.entities.CardEntity_;
import fr.psalles.kmdeckbuilder.models.entities.Identity_;
import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.models.enums.CardType;
import fr.psalles.kmdeckbuilder.models.enums.God;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CardSpecification {

    public static Specification<CardEntity> filterByCardType(CardType cardType) {
        return (root, query, builder) -> {
            if (cardType == null) {
                return builder.and();
            }
            return builder.equal(root.get(CardEntity_.cardType), cardType);
        };
    }

    public static Specification<CardEntity> filterByHpGreaterThan(Integer hpMin) {
        return (root, query, builder) -> {
            if (hpMin == null) {
                return builder.and();
            } else {
                return builder.greaterThanOrEqualTo(root.get(CardEntity_.life), hpMin);
            }
        };
    }

    public static Specification<CardEntity> filterByHpLessThan(Integer hpMax) {
        return (root, query, builder) -> {
            if (hpMax == null) {
                return builder.and();
            } else {
                return builder.lessThanOrEqualTo(root.get(CardEntity_.life), hpMax);
            }
        };
    }

    public static Specification<CardEntity> filterByApGreaterThan(Integer apMin) {
        return (root, query, builder) -> {
            if (apMin == null) {
                return builder.and();
            } else {
                return builder.greaterThanOrEqualTo(root.get(CardEntity_.costAP), apMin);
            }
        };
    }

    public static Specification<CardEntity> filterByApLessThan(Integer apMax) {
        return (root, query, builder) -> {
            if (apMax == null) {
                return builder.and();
            } else {
                return builder.lessThanOrEqualTo(root.get(CardEntity_.costAP), apMax);
            }
        };
    }

    public static Specification<CardEntity> filterByAtGreaterThan(Integer atMin) {
        return (root, query, builder) -> {
            if (atMin == null) {
                return builder.and();
            } else {
                return builder.greaterThanOrEqualTo(root.get(CardEntity_.attack), atMin);
            }
        };
    }

    public static Specification<CardEntity> filterByAtLessThan(Integer atMax) {
        return (root, query, builder) -> {
            if (atMax == null) {
                return builder.and();
            } else {
                return builder.lessThanOrEqualTo(root.get(CardEntity_.attack), atMax);
            }
        };
    }

    public static Specification<CardEntity> filterByMpLessThan(Integer mpMax) {
        return (root, query, builder) -> {
            if (mpMax == null) {
                return builder.and();
            } else {
                return builder.lessThanOrEqualTo(root.get(CardEntity_.movementPoint), mpMax);
            }
        };
    }

    public static Specification<CardEntity> filterByMpGreaterThan(Integer mpMin) {
        return (root, query, builder) -> {
            if (mpMin == null) {
                return builder.and();
            } else {
                return builder.greaterThanOrEqualTo(root.get(CardEntity_.movementPoint), mpMin);
            }
        };
    }

    public static Specification<CardEntity> filterByGod(List<God> gods) {
        return (root, query, builder) -> {
            if (gods == null) {
                return builder.and();
            } else {
                CriteriaBuilder.In<God> inClause = builder.in(root.get(CardEntity_.godType));
                for (God god : gods) {
                    inClause.value(god);
                }
                return builder.and(inClause);
            }
        };
    }

    public static Specification<CardEntity> filterByRarity(CardRarity rarity) {
        return (root, query, builder) -> {
            if (rarity == null) {
                return builder.and();
            } else {
                return builder.equal(root.get(CardEntity_.rarity), rarity);
            }
        };
    }


    public static Specification<CardEntity> filterByLanguage(String language) {
        return (root, query, builder) -> {
            if (language == null) {
                return builder.and();
            } else {
                return builder.equal(root.get(CardEntity_.cardIdentity).get(Identity_.language), language);
            }
        };
    }

    public static Specification<CardEntity> filterByNameLikeContent(String name) {
        return (root, query, builder) -> {
            if (name == null) {
                return builder.and();
            } else {
                return builder.like(builder.lower(root.get(CardEntity_.name)), "%"+ name.toLowerCase()+"%");
            }
        };
    }

    public static Specification<CardEntity> filterByDescriptionLikeContent(String content) {
        return (root, query, builder) -> {
            if (content == null) {
                return builder.and();
            } else {
               return builder.like(builder.lower(root.get(CardEntity_.description)), "%"+ content.toLowerCase()+"%");
            }
        };
    }

    public static Specification<CardEntity> filterByFamily(Integer family) {
        return (root, query, builder) -> {
            if (family == null) {
                return builder.and();
            } else {
                return builder.or(builder.equal(root.get(CardEntity_.family1), family),
                        builder.equal(root.get(CardEntity_.family2), family));
            }
        };
    }

}
