package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.commons.exceptions.ForbiddenException;
import fr.psalles.kmdeckbuilder.models.CardDto;
import fr.psalles.kmdeckbuilder.models.DeckDto;
import fr.psalles.kmdeckbuilder.models.HighlightDto;
import fr.psalles.kmdeckbuilder.models.SimpleTagDto;
import fr.psalles.kmdeckbuilder.models.entities.*;
import fr.psalles.kmdeckbuilder.models.entities.embedded.*;
import fr.psalles.kmdeckbuilder.models.entities.projections.FavoriteCount;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.models.responses.SavedDeckResponse;
import fr.psalles.kmdeckbuilder.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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
    private TagAssociationRepository tagAssociationRepository;

    @Autowired
    private HighlightRepository highlightRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private LastVersionRepository lastVersionRepository;


    // au lancement de l'appli, rafraichir le compteur de likes en base
    @PostConstruct
    public void init() {
        deckRepository.initLikesOnDecks();
    }

    // une fois par semaine, rafraichir le compteur de likes en base
    @Scheduled(cron = "0 0 0 * * SUN")
    public void refresh() {
        deckRepository.initLikesOnDecks();
    }

    public void deleteDeckById(String id) {
        favoriteRepository.deleteByDeckEntity(id);
        associationRepository.deleteByDeckEntity(id);
        highlightRepository.deleteByDeckEntity(id);
        tagAssociationRepository.deleteByDeckEntity(id);
        deckRepository.deleteByDeckEntity(id);
    }

    public void deleteDeckById(String id, Integer version) {
        associationRepository.deleteByDeckEntity(id, version);
        highlightRepository.deleteByDeckEntity(id, version);
        tagAssociationRepository.deleteByDeckEntity(id, version);
        deckRepository.deleteByDeckEntity(id, version);
    }

    public boolean deleteDeck(String id) {
        // Some control IN CASE SOMETHING GOES WRONG IN FRONT CHECK because of circonstances of the action.
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        DeckEntity deckEntity = deckRepository.findLastVersionForDeckId(id);

        if (userId.equals(deckEntity.getUserId().getUserId())) {
            deleteDeckById(id);
            return true;
        } else throw new ForbiddenException("Le deck ne vous appartient pas");
    }

    public SavedDeckResponse saveDeck(DeckCreateForm deck) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici

        DeckEntity entity = new DeckEntity();

        entity.setName(deck.getName());
        entity.setGod(deck.getGod());
        entity.setCostAP(deck.getCards().stream().map(a -> a.getCostAP() * a.getCount()).reduce(0, Integer::sum));
        entity.setCostDust(deck.getCards().stream().map(a -> a.getRarity().getDust() * a.getCount()).reduce(0, Integer::sum));
        entity.setUserId(user);
        entity.setDescription(deck.getDescription());

        List<CardAssociation> associations = deck.getCards().stream().map(card -> {
            CardAssociation cardAssociation = new CardAssociation();
            cardAssociation.setId(new AssociationIdentity(entity.getId(), card.getId()));
            cardAssociation.setNbrExemplaires(card.getCount());
            return cardAssociation;
        }).toList();

        boolean isDuplicate = false;
        if (deck.getDeckId() != null) {
            isDuplicate = true;
            DeckEntity oldEntity = deckRepository.findLastVersionForDeckId(deck.getDeckId());
            DeckIdentity deckIdentity = oldEntity.getId();

            entity.setCreationDate(oldEntity.getCreationDate());
            entity.setFavoriteCount(oldEntity.getFavoriteCount());

            List<CardAssociation> oldCardsAssociation = oldEntity.getCards();

            for (CardAssociation association : associations) {
                if (!oldCardsAssociation.contains(association)) {
                    isDuplicate = false;
                    break;
                }
            }

            if (!isDuplicate) {
                lastVersionRepository.delete(new LastVersionEntity(deckIdentity));
                deckIdentity.setVersion(deckIdentity.getVersion() + 1);
                entity.setId(deckIdentity);
                lastVersionRepository.save(new LastVersionEntity(deckIdentity));
            } else {
                deleteDeckById(deckIdentity.getDeckId(), deckIdentity.getVersion());
                entity.setId(deckIdentity);
            }
        } else {
            DeckIdentity deckIdentity = new DeckIdentity(UUID.randomUUID().toString(), 1);
            entity.setId(deckIdentity);
            lastVersionRepository.save(new LastVersionEntity(deckIdentity));
            entity.setCreationDate(LocalDateTime.now());
        }

        associations = deck.getCards().stream().map(card -> {
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
        DeckIdentity identity = deckRepository.save(entity).getId();

        if (deck.getDeckId() != null) {
            if (isDuplicate) {
                log.info("{} modifie la description du deck {}", user.getUsername(), deck.getName());
            } else {
                log.info("{} modifie le deck {}", user.getUsername(), deck.getName());
            }
        } else {
            log.info("{} ajoute le deck {}", user.getUsername(), deck.getName());
        }
        return SavedDeckResponse.builder().deckId(identity.getDeckId()).version(identity.getVersion()).build();
    }

    public Page<DeckDto> findDecks(DeckSearchForm form, boolean authenticated) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<DeckEntity> page = deckRepository.findAll(
                filterByGod(form.getGods())
                        .and(filterLastVersion())
                        .and(filterFavoritesOnly(userId, authenticated && form.isFavoritesOnly()))
                        .and(filterByCards(form.getCards()))
                        .and(filterByTags(form.getTags(), false))
                        .and(filterByTags(form.getNegativeTags(), true))
                        .and(filterByOwners(form.getUsers(), false))
                        .and(filterByOwners(form.getNegativeUsers(), true))
                        .and(filterByDust(form.getDustCost(), form.isDustGeq()))
                        .and(filterByAp(form.getActionPointCost(), form.isActionCostGeq()))
                        .and(filterByNameLikeContent(form.getContent())
                        )
                , PageRequest.of(form.getPage(), form.getPageSize(), Sort.Direction.DESC, form.getSearchBy().getFieldName()));

        List<String> userFavs = new ArrayList<>();
        if (authenticated) {
            UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
            userFavs.addAll(user.getFavorites().stream().map(x -> x.getId().getDeckId()).toList());
        }

        List<SimpleTagDto> allTags = tagsService.getTagsByLanguage(form.getLanguage());

        return page.map(entity -> {
            List<Integer> tagIds = entity.getTags().stream().map(a -> a.getId().getTagId()).toList();
            List<SimpleTagDto> tags = allTags.stream().filter(tag -> tagIds.contains(tag.getId())).toList();
            return DeckDto.builder()
                    .deckId(entity.getId().getDeckId())
                    .name(entity.getName())
                    .owner(entity.getUserId().getUsername())
                    .owned(entity.getUserId().getUserId().equals(userId))
                    .god(entity.getGod())
                    .creationDate(entity.getCreationDate())
                    .costAP(entity.getCostAP())
                    .tags(tags)
                    .version(entity.getId().getVersion())
                    .favoriteCount(entity.getFavoriteCount())
                    .liked(userFavs.contains(entity.getId().getDeckId()))
                    .highlights(entity.getHighlights().stream()
                            .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                            .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                            .collect(toList()))
                    .costDust(entity.getCostDust())
                    .build();
        });
    }

    public Page<DeckDto> findFavoriteDecks(Language language) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
        List<String> userFavs = user.getFavorites().stream()
                .sorted(Comparator.comparing(FavoriteAssociation::getFavoriteDate).reversed()).map(x -> x.getId().getDeckId())
                .limit(30).toList();

        Page<DeckEntity> page = deckRepository.findAll(
                filterLastVersion().and(filterByIdIn(userFavs))
                , PageRequest.of(0, 30));

        List<SimpleTagDto> allTags = tagsService.getTagsByLanguage(language);

        return page.map(entity -> {
            List<Integer> tagIds = entity.getTags().stream().map(a -> a.getId().getTagId()).toList();
            List<SimpleTagDto> tags = allTags.stream().filter(tag -> tagIds.contains(tag.getId())).toList();
            return DeckDto.builder()
                    .deckId(entity.getId().getDeckId())
                    .name(entity.getName())
                    .owner(entity.getUserId().getUsername())
                    .owned(entity.getUserId().getUserId().equals(userId))
                    .god(entity.getGod())
                    .tags(tags)
                    .creationDate(entity.getCreationDate())
                    .costAP(entity.getCostAP())
                    .version(entity.getId().getVersion())
                    .favoriteCount(entity.getFavoriteCount())
                    .liked(true)
                    .highlights(entity.getHighlights().stream()
                            .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                            .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                            .collect(toList()))
                    .costDust(entity.getCostDust())
                    .build();
        });
    }

    public DeckDto getDeck(String id, Integer version, Language language, boolean authenticated) {
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

        List<Integer> tagIds = deckEntity.getTags().stream().map(a -> a.getId().getTagId()).toList();
        List<SimpleTagDto> tags = tagsService.getTagsByLanguage(language).stream().filter(tag -> tagIds.contains(tag.getId())).toList();

        List<String> userFavs = new ArrayList<>();

        if (authenticated) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
            userFavs.addAll(user.getFavorites().stream().map(x -> x.getId().getDeckId()).toList());
        }

        return DeckDto.builder()
                .deckId(deckEntity.getId().getDeckId())
                .name(deckEntity.getName())
                .owner(deckEntity.getUserId().getUsername())
                .owned(authenticated && deckEntity.getUserId().getUserId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                .cards(cardDtos)
                .tags(tags)
                .god(deckEntity.getGod())
                .description(deckEntity.getDescription())
                .creationDate(deckEntity.getCreationDate())
                .costAP(deckEntity.getCostAP())
                .version(deckEntity.getId().getVersion())
                .liked(userFavs.contains(deckEntity.getId().getDeckId()))
                .versions(versions)
                .favoriteCount(deckEntity.getFavoriteCount())
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
        DeckEntity deck = deckRepository.findLastVersionForDeckId(deckId);

        if (!deck.getUserId().getUserId().equals(userId)) {
            UserEntity user = userRepository.findByUserId(userId);

            FavoriteAssociation favoriteAssociation =
                    new FavoriteAssociation(new FavoriteAssociationIdentity(deckId, userId), LocalDateTime.now());
            user.getFavorites().add(favoriteAssociation);
            userRepository.save(user);

            deckRepository.addLikeOnDeck(deckId);

            return true;
        }

        return false;
    }

    public boolean removeFavoriteDeck(String deckId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        FavoriteAssociation toBeRemoved = user.getFavorites().stream()
                .filter(fav -> fav.getId().getDeckId().equals(deckId)).toList().getFirst();
        user.getFavorites().remove(toBeRemoved);
        toBeRemoved.setId(null);
        userRepository.save(user);

        deckRepository.removeLikeOnDeck(deckId);

        return true;
    }


}
