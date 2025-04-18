package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.CardDto;
import fr.psalles.kmdeckbuilder.models.DeckDto;
import fr.psalles.kmdeckbuilder.models.HighlightDto;
import fr.psalles.kmdeckbuilder.models.entities.*;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.repositories.CardRepository;
import fr.psalles.kmdeckbuilder.repositories.DeckRepository;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.psalles.kmdeckbuilder.models.entities.specification.DeckSpecification.*;
import static java.util.stream.Collectors.toList;


@Slf4j
@Service
public class DeckService {

    @Autowired
    private CardService cardService;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;

    public Long saveDeck(DeckCreateForm deck) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici

        DeckEntity entity = new DeckEntity();

        entity.setName(deck.getName());
        entity.setGod(deck.getGod());
        entity.setCreationDate(LocalDateTime.now());
        entity.setCostAP(deck.getCards().stream().map(a -> a.getCostAP() * a.getCount()).reduce(0, Integer::sum));
        entity.setCostDust(deck.getCards().stream().map(a -> a.getRarity().getDust() * a.getCount()).reduce(0, Integer::sum));
        entity.setUserId(user);
        entity.setDescription(deck.getDescription());

        DeckEntity savedDeck = deckRepository.save(entity);

        List<CardAssociation> associations = deck.getCards().stream().map(card -> {
            CardAssociation cardAssociation = new CardAssociation();
            cardAssociation.setId(new AssociationIdentity(savedDeck.getDeckId(), card.getId()));
            cardAssociation.setNbrExemplaires(card.getCount());
//            cardAssociation.setHighlightOrder(card.getHightlight());
            return cardAssociation;
        }).toList();
        savedDeck.getCards().addAll(associations);

        List<DeckHighlight> highlights = deck.getCards().stream()
                .filter(card -> card.getHighlight() != null).map(card -> {
            DeckHighlight cardAssociation = new DeckHighlight();
            cardAssociation.setId(new AssociationIdentity(savedDeck.getDeckId(), card.getId()));
            cardAssociation.setHighlightOrder(card.getHighlight());
            return cardAssociation;
        }).toList();

        savedDeck.getHighlights().addAll(highlights);
        return deckRepository.save(savedDeck).getDeckId();
    }

    public Page<DeckDto> findDecks(DeckSearchForm form) {
        Page<DeckEntity> page = deckRepository.findAll(
                filterByGod(form.getGods())
                        .and(filterByCards(form.getCards()))
                        .and(filterByOwners(form.getUsers()))
                        .and(filterByDust(form.getDustCost(), form.getDustGeq()))
                        .and(filterByAp(form.getActionPointCost(), form.getActionCostGeq()))
                        .and(filterByNameLikeContent(form.getContent())
                        )
                , PageRequest.of(0, 20, Sort.Direction.ASC, "deckId"));

        return page.map(entity -> DeckDto.builder()
                .deckId(entity.getDeckId())
                .name(entity.getName())
                .owner(entity.getUserId().getUsername())
                .god(entity.getGod())
                .creationDate(entity.getCreationDate())
                .costAP(entity.getCostAP())
                .highlights(entity.getHighlights().stream()
                        .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                        .map(a-> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                        .collect(toList()))
                .costDust(entity.getCostDust())
                .build());
    }

    public DeckDto getDeck(String id, Language language) {
        DeckEntity deckEntity = deckRepository.findById(id).get();

        Map<Integer, CardAssociation> cardMap = deckEntity.getCards().stream()
                .collect(Collectors.toMap(x -> x.getId().getCardId(), Function.identity(), (a, b) -> a));

        // pour récupérer les cartes dans la langue de l'utilisateur
        List<Identity> cardIds = deckEntity.getCards().stream().map(cardAssociation -> new Identity(cardAssociation.getId().getCardId(), language)).toList();

        List<CardEntity> cards = cardRepository.findByCardIdentityIsIn(cardIds);
        List<CardDto> cardDtos = cards.stream()
                .map(entity -> CardDto.builder()
                        .id(entity.getCardIdentity().getId())
                        .cardType(entity.getCardType())
                        .rarity(entity.getRarity())
                        .costAP(entity.getCostAP())
                        .godType(entity.getGodType())
                        .name(entity.getName())
                        .infiniteName(entity.getInfiniteName())
                        .infiniteLevel(entity.getInfiniteLevel())
                        .life(entity.getLife())
                        .attack(entity.getAttack())
                        .movementPoint(entity.getMovementPoint())
                        .count(cardMap.get(entity.getCardIdentity().getId()).getNbrExemplaires())
                        .build())
                .toList();

        return DeckDto.builder()
                .deckId(deckEntity.getDeckId())
                .name(deckEntity.getName())
                .owner(deckEntity.getUserId().getUsername())
                .cards(cardDtos)
                .god(deckEntity.getGod())
                .description(deckEntity.getDescription())
                .creationDate(deckEntity.getCreationDate())
                .costAP(deckEntity.getCostAP())
                .costDust(deckEntity.getCostDust())
                .cards(cardDtos).build();
    }

    public List<UserCount> loadDeckOwners() {
        return deckRepository.findAllProjectedBy();
    }

}
