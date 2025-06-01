package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.CardDto;
import fr.psalles.kmdeckbuilder.models.DeckDto;
import fr.psalles.kmdeckbuilder.models.HighlightDto;
import fr.psalles.kmdeckbuilder.models.entities.*;
import fr.psalles.kmdeckbuilder.models.entities.embedded.*;
import fr.psalles.kmdeckbuilder.models.entities.projections.FavoriteCount;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.models.responses.SavedDeckResponse;
import fr.psalles.kmdeckbuilder.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.psalles.kmdeckbuilder.models.entities.specification.DeckSpecification.*;
import static java.util.stream.Collectors.toList;


@Slf4j
@Service
public class DeckService {

    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private LastVersionRepository lastVersionRepository;

    public SavedDeckResponse saveDeck(DeckCreateForm deck) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici

        DeckEntity entity = new DeckEntity();

        if (deck.getDeckId() != null) {
            DeckEntity oldEntity = deckRepository.findLastVersionForDeckId(deck.getDeckId());
            DeckIdentity deckIdentity = oldEntity.getId();
            lastVersionRepository.delete(new LastVersionEntity(deckIdentity));
            deckIdentity.setVersion(deckIdentity.getVersion() + 1);
            entity.setId(deckIdentity);
            lastVersionRepository.save(new LastVersionEntity(deckIdentity));
        } else {
            DeckIdentity deckIdentity = new DeckIdentity(UUID.randomUUID().toString(), 1);
            entity.setId(deckIdentity);
            lastVersionRepository.save(new LastVersionEntity(deckIdentity));
        }

        entity.setName(deck.getName());
        entity.setGod(deck.getGod());
        entity.setCreationDate(LocalDateTime.now());
        entity.setCostAP(deck.getCards().stream().map(a -> a.getCostAP() * a.getCount()).reduce(0, Integer::sum));
        entity.setCostDust(deck.getCards().stream().map(a -> a.getRarity().getDust() * a.getCount()).reduce(0, Integer::sum));
        entity.setUserId(user);
        entity.setDescription(deck.getDescription());

//        DeckEntity savedDeck = deckRepository.save(entity);

        List<CardAssociation> associations = deck.getCards().stream().map(card -> {
            CardAssociation cardAssociation = new CardAssociation();
            cardAssociation.setId(new AssociationIdentity(entity.getId(), card.getId()));
            cardAssociation.setNbrExemplaires(card.getCount());
            return cardAssociation;
        }).toList();
        entity.getCards().addAll(associations);

        List<TagAssociation> tags = deck.getTags().stream().map(tag -> {
            TagAssociation tagAssociation = new TagAssociation();
            tagAssociation.setId(new TagAssociationIdentity(entity.getId(), tag));
            return tagAssociation;
        }).toList();
        entity.getTags().addAll(tags);

        List<DeckHighlight> highlights = deck.getCards().stream()
                .filter(card -> card.getHighlight() != null).map(card -> {
                    DeckHighlight cardAssociation = new DeckHighlight();
                    cardAssociation.setId(new AssociationIdentity(entity.getId(), card.getId()));
                    cardAssociation.setHighlightOrder(card.getHighlight());
                    return cardAssociation;
                }).toList();

        entity.getHighlights().addAll(highlights);
        DeckIdentity identity =  deckRepository.save(entity).getId();
        return SavedDeckResponse.builder().deckId(identity.getDeckId()).version(identity.getVersion()).build();
    }

    public Page<DeckDto> findDecks(DeckSearchForm form, boolean authenticated) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<DeckEntity> page = deckRepository.findAll(
                filterByGod(form.getGods())
                        .and(filterLastVersion())
                        .and(filterFavoritesOnly(userId, authenticated && Boolean.TRUE.equals(form.getFavoritesOnly())))
                        .and(filterByCards(form.getCards()))
                        .and(filterByTags(form.getTags()))
                        .and(filterByOwners(form.getUsers()))
                        .and(filterByDust(form.getDustCost(), form.getDustGeq()))
                        .and(filterByAp(form.getActionPointCost(), form.getActionCostGeq()))
                        .and(filterByNameLikeContent(form.getContent())
                        )
                , PageRequest.of(0, 20, Sort.Direction.DESC, "creationDate"));

        List<FavoriteCount> favs = favoriteRepository.countFavorites(page.stream().map(deck -> deck.getId().getDeckId()).toList());
        Map<String, Integer> mappedFavs = favs.stream().collect(Collectors.toMap(FavoriteCount::getDeckId, FavoriteCount::getCount, (a, b) -> a));

        List<String> userFavs = new ArrayList<>();
        if (authenticated) {
            UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
            userFavs.addAll(user.getFavorites().stream().map(x -> x.getId().getDeckId()).toList());
        }

        return page.map(entity -> DeckDto.builder()
                .deckId(entity.getId().getDeckId())
                .name(entity.getName())
                .owner(entity.getUserId().getUsername())
                .god(entity.getGod())
                .creationDate(entity.getCreationDate())
                .costAP(entity.getCostAP())
                .version(entity.getId().getVersion())
                .favoriteCount(mappedFavs.getOrDefault(entity.getId().getDeckId(), 0))
                .liked(userFavs.contains(entity.getId().getDeckId()))
                .highlights(entity.getHighlights().stream()
                        .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                        .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                        .collect(toList()))
                .costDust(entity.getCostDust())
                .build());
    }

    public Page<DeckDto> findFavoriteDecks() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
        List<String> userFavs = user.getFavorites().stream()
                .sorted(Comparator.comparing(FavoriteAssociation::getFavoriteDate).reversed()).map(x -> x.getId().getDeckId())
                .limit(30).toList();

        Page<DeckEntity> page = deckRepository.findAll(
                filterLastVersion().and(filterByIdIn(userFavs))
                , PageRequest.of(0, 30));

        List<FavoriteCount> favs = favoriteRepository.countFavorites(page.stream().map(deck -> deck.getId().getDeckId()).toList());
        Map<String, Integer> mappedFavs = favs.stream().collect(Collectors.toMap(FavoriteCount::getDeckId, FavoriteCount::getCount, (a, b) -> a));

        return page.map(entity -> DeckDto.builder()
                .deckId(entity.getId().getDeckId())
                .name(entity.getName())
                .owner(entity.getUserId().getUsername())
                .god(entity.getGod())
                .creationDate(entity.getCreationDate())
                .costAP(entity.getCostAP())
                .version(entity.getId().getVersion())
                .favoriteCount(mappedFavs.get(entity.getId().getDeckId())) // si on charge les favs d'un user, y'a au moins 1 like
                .liked(true)
                .highlights(entity.getHighlights().stream()
                        .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                        .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                        .collect(toList()))
                .costDust(entity.getCostDust())
                .build());
    }

    public DeckDto getDeck(String id, Integer version, Language language) {
        DeckEntity deckEntity = deckRepository.findById(new DeckIdentity(id, version));
        List<Integer> versions = deckRepository.findVersionNumberForDeckId(id);

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
                .deckId(deckEntity.getId().getDeckId())
                .name(deckEntity.getName())
                .owner(deckEntity.getUserId().getUsername())
                .cards(cardDtos)
                .god(deckEntity.getGod())
                .description(deckEntity.getDescription())
                .creationDate(deckEntity.getCreationDate())
                .costAP(deckEntity.getCostAP())
                .version(deckEntity.getId().getVersion())
                .versions(versions)
                .highlights(deckEntity.getHighlights().stream()
                        .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                        .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                        .collect(toList()))
                .costDust(deckEntity.getCostDust())
                .cards(cardDtos).build();
    }

    public List<UserCount> loadDeckOwners() {
        return deckRepository.countOwners();
    }

    public boolean addFavoriteDeck(String deckId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        FavoriteAssociation favoriteAssociation =
                new FavoriteAssociation(new FavoriteAssociationIdentity(deckId, userId), LocalDateTime.now());
        user.getFavorites().add(favoriteAssociation);
        userRepository.save(user);

        return true;
    }

    public boolean removeFavoriteDeck(String deckId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        FavoriteAssociation toBeRemoved = user.getFavorites().stream()
                .filter(fav -> fav.getId().getDeckId().equals(deckId)).toList().getFirst();
        user.getFavorites().remove(toBeRemoved);
        toBeRemoved.setId(null);
        userRepository.save(user);
        return true;
    }


}
