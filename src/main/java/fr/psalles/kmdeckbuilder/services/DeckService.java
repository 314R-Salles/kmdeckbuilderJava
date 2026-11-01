package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.commons.exceptions.ForbiddenException;
import fr.psalles.kmdeckbuilder.commons.exceptions.ResourceNotFoundException;
import fr.psalles.kmdeckbuilder.models.CardDto;
import fr.psalles.kmdeckbuilder.models.DeckDto;
import fr.psalles.kmdeckbuilder.models.HighlightDto;
import fr.psalles.kmdeckbuilder.models.SimpleTagDto;
import fr.psalles.kmdeckbuilder.models.entities.*;
import fr.psalles.kmdeckbuilder.models.entities.embedded.AssociationIdentity;
import fr.psalles.kmdeckbuilder.models.entities.embedded.DeckIdentity;
import fr.psalles.kmdeckbuilder.models.entities.embedded.Identity;
import fr.psalles.kmdeckbuilder.models.entities.embedded.TagAssociationIdentity;
import fr.psalles.kmdeckbuilder.models.entities.projections.DeckView;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.enums.SearchBy;
import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.models.responses.SavedDeckResponse;
import fr.psalles.kmdeckbuilder.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.psalles.kmdeckbuilder.models.entities.specification.DeckSpecification.*;


@Slf4j
@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TagAssociationRepository tagAssociationRepository;
    private final HighlightRepository highlightRepository;
    private final AssociationRepository associationRepository;
    private final TagsService tagsService;
    private final FavoriteRepository favoriteRepository;
    private final LastVersionRepository lastVersionRepository;
    private final UserService userService;

    public DeckService(DeckRepository deckRepository, UserRepository userRepository, CardRepository cardRepository, TagAssociationRepository tagAssociationRepository, HighlightRepository highlightRepository, AssociationRepository associationRepository, TagsService tagsService, FavoriteRepository favoriteRepository, LastVersionRepository lastVersionRepository, UserService userService) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.tagAssociationRepository = tagAssociationRepository;
        this.highlightRepository = highlightRepository;
        this.associationRepository = associationRepository;
        this.tagsService = tagsService;
        this.favoriteRepository = favoriteRepository;
        this.lastVersionRepository = lastVersionRepository;
        this.userService = userService;
    }

    // au lancement de l'appli, rafraichir le compteur de likes en base
    @PostConstruct
    public void init() {
        log.info("startup likes et favoris");
        deckRepository.initLikesOnDecks();
        favoriteRepository.cleanFavoriteOwner();
    }

    // une fois par semaine, rafraichir le compteur de likes en base
    @Scheduled(cron = "0 0 0 * * SUN")
    public void refresh() {
        log.info("scheduled cron likes");
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
        DeckEntity deckEntity = deckRepository.findLastVersionForDeckId(id);

        // Some control IN CASE SOMETHING GOES WRONG IN FRONT CHECK because of circonstances of the action.
        String userId = UserService.getUserIdFromSecurityContext();
        if (!userId.equals(deckEntity.getUserId().getUserId())) {
            throw new ForbiddenException("Le deck ne vous appartient pas");
        }
        deleteDeckById(id);
        return true;

    }

    @Transactional
    public SavedDeckResponse saveDeck(DeckCreateForm deck) {
        UserEntity user = userService.getUserReferenceFromSecurityContext();

        DeckEntity entity = new DeckEntity();

        entity.setName(deck.getName());
        entity.setGod(deck.getGod());
        entity.setCostAP(deck.getCards().stream().map(a -> a.getCostAP() * a.getCount()).reduce(0, Integer::sum));
        entity.setCostDust(deck.getCards().stream().map(a -> a.getRarity().getDust() * a.getCount()).reduce(0, Integer::sum));
        entity.setUserId(user);
        entity.setVideoLink(deck.getVideoLink());
        entity.setDescription(deck.getDescription());

        List<CardAssociation> associations = deck.getCards().stream().map(card -> {
            CardAssociation cardAssociation = new CardAssociation();
            cardAssociation.setId(new AssociationIdentity(entity.getId(), card.getId()));
            cardAssociation.setNbrExemplaires(card.getCount());
            return cardAssociation;
        }).toList();

        boolean hasSameCards = false;

        if (deck.getDeckId() != null) {
            DeckEntity oldEntity = deckRepository.findLastVersionForDeckId(deck.getDeckId());
            if (oldEntity == null) {
                throw new ResourceNotFoundException("Ce deck n'existe pas");
            }
            if (!oldEntity.getUserId().getUserId().equals(user.getUserId())) {
                throw new ForbiddenException("Le deck ne vous appartient pas");
            }
            DeckIdentity deckIdentity = oldEntity.getId();

            entity.setCreationDate(oldEntity.getCreationDate());
            entity.setFavoriteCount(oldEntity.getFavoriteCount());

            List<CardAssociation> oldCardsAssociation = oldEntity.getCards();

            hasSameCards = true;
            for (CardAssociation association : associations) {
                if (!oldCardsAssociation.contains(association)) {
                    hasSameCards = false;
                    break;
                }
            }

            if (hasSameCards) {
                deleteDeckById(deckIdentity.getDeckId(), deckIdentity.getVersion());
                entity.setMinorVersion(oldEntity.getMinorVersion() + 1);
                entity.setId(deckIdentity);
            } else {
                lastVersionRepository.delete(new LastVersionEntity(deckIdentity));
                deckIdentity.setVersion(deckIdentity.getVersion() + 1);
                entity.setMinorVersion(0);
                entity.setId(deckIdentity);
                lastVersionRepository.save(new LastVersionEntity(deckIdentity));
            }
        } else {
            DeckIdentity deckIdentity = new DeckIdentity(UUID.randomUUID().toString(), 1);
            entity.setMinorVersion(0);
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
        DeckEntity savedDeck = deckRepository.save(entity);

        if (deck.getDeckId() != null) {
            if (hasSameCards) {
                log.info("{} modifie la description du deck {}", user.getUserId(), deck.getName());
            } else {
                log.info("{} modifie le deck {}", user.getUserId(), deck.getName());
            }
        } else {
            log.info("{} ajoute le deck {}", user.getUserId(), deck.getName());
        }
        return SavedDeckResponse.builder().deckId(savedDeck.getId().getDeckId()).version(savedDeck.getId().getVersion()).minorVersion(savedDeck.getMinorVersion()).build();
    }

    public Page<DeckDto> findDecks(DeckSearchForm form) {
        String userId = UserService.getUserIdFromSecurityContext();
        boolean authenticated = UserService.isAuthenticatedInSecurityContext();

        String searchBy = getSearchByWithDefault(form);
        Sort.Direction orderBy = getOrderByWithDefault(form);
        Page<DeckEntity> page = deckRepository.findAll(
                filterByGod(form.getGods())
                        .and(filterLastVersion())
                        .and(filterFavoritesOnly(userId, authenticated && form.isFavoritesOnly()))
                        .and(filterByCards(form.getCards()))
                        .and(filterByTags(form.getTags(), false))
                        .and(filterByTags(form.getNegativeTags(), true))
                        .and(filterByOwners(form.getUsers(), false))
                        .and(filterByOwners(form.getNegativeUsers(), true))
//                        .and(filterByDust(form.getDustCost(), form.isDustGeq()))
//                        .and(filterByAp(form.getActionPointCost(), form.isActionCostGeq()))
                        .and(filterByNameLikeContent(form.getContent())
                        )
                , PageRequest.of(form.getPage(), form.getPageSize(), orderBy, searchBy));

        List<String> userFavs = new ArrayList<>();
        if (authenticated) {
            UserEntity user = userRepository.findByUserId(userId);
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
                    .minorVersion(entity.getMinorVersion())
                    .favoriteCount(entity.getFavoriteCount())
                    .liked(userFavs.contains(entity.getId().getDeckId()))
                    .highlights(entity.getHighlights().stream()
                            .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                            .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                            .toList())
                    .costDust(entity.getCostDust())
                    .build();
        });
    }

    private static String getSearchByWithDefault(DeckSearchForm form) {
        return form.getSearchBy() == null ? SearchBy.RECENT.getFieldName() : form.getSearchBy().getFieldName();
    }

    private static Sort.Direction getOrderByWithDefault(DeckSearchForm form) {
        return form.getOrderBy() == null ? Sort.Direction.DESC : form.getOrderBy();
    }

    public Page<DeckDto> findFavoriteDecks(Language language) {
        String userId = UserService.getUserIdFromSecurityContext();

        UserEntity user = userRepository.findByUserId(userId);
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
                    .minorVersion(entity.getMinorVersion())
                    .favoriteCount(entity.getFavoriteCount())
                    .liked(true)
                    .highlights(entity.getHighlights().stream()
                            .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                            .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                            .toList())
                    .costDust(entity.getCostDust())
                    .build();
        });
    }

    public DeckDto getDeck(String id, Integer version, Language language) {
        String userId = UserService.getUserIdFromSecurityContext();
        boolean authenticated = UserService.isAuthenticatedInSecurityContext();
        DeckEntity deckEntity = deckRepository.findById(new DeckIdentity(id, version));
        if (deckEntity == null) {
            log.error("{} essaie d'ouvrir un deck inexistant : {}/{}", userId, id, version);
            throw new ResourceNotFoundException("Ce deck n'existe pas");
        }

        List<String> versions = deckRepository.findVersionNumbersForDeckId(id);
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
            UserEntity user = userRepository.findByUserId(userId);
            userFavs.addAll(user.getFavorites().stream().map(x -> x.getId().getDeckId()).toList());
        }

        return DeckDto.builder()
                .deckId(deckEntity.getId().getDeckId())
                .name(deckEntity.getName())
                .owner(deckEntity.getUserId().getUsername())
                .owned(authenticated && deckEntity.getUserId().getUserId().equals(userId))
                .cards(cardDtos)
                .tags(tags)
                .god(deckEntity.getGod())
                .description(deckEntity.getDescription())
                .creationDate(deckEntity.getCreationDate())
                .costAP(deckEntity.getCostAP())
                .version(deckEntity.getId().getVersion())
                .minorVersion(deckEntity.getMinorVersion())
                .liked(userFavs.contains(deckEntity.getId().getDeckId()))
                .versions(versions)
                .videoLink(deckEntity.getVideoLink())
                .favoriteCount(deckEntity.getFavoriteCount())
                .highlights(deckEntity.getHighlights().stream()
                        .sorted(Comparator.comparingInt(DeckHighlight::getHighlightOrder))
                        .map(a -> HighlightDto.builder().highlightOrder(a.getHighlightOrder()).cardId(a.getId().getCardId()).build())
                        .toList())
                .costDust(deckEntity.getCostDust())
                .cards(cardDtos).build();
    }

    public DeckView getDeckForCrawlers(String id, int version) {
        return deckRepository.getDeckView(id, version);
    }

    // mettre un cache? qui est wipe à chaque création/suppression de deck?
    public List<UserCount> loadDeckOwners() {
        return deckRepository.countOwners();
    }

}
