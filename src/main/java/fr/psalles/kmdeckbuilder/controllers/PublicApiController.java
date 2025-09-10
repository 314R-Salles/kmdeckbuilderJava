package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.*;
import fr.psalles.kmdeckbuilder.models.entities.IllustrationNameEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.CardView;
import fr.psalles.kmdeckbuilder.models.entities.projections.DeckView;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.requests.CardSearchForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.models.responses.Media;
import fr.psalles.kmdeckbuilder.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    private final MediaService mediaService;
    private final NewsService newsService;
    private final UserService userService;
    private final CardService cardService;
    private final CardIllustrationService cardIllustrationService;
    private final DeckService deckService;
    private final TagsService tagsService;

    @Autowired
    public PublicApiController(DeckService deckService,
                               MediaService mediaService,
                               NewsService newsService, UserService userService,
                               CardService cardService,
                               TagsService tagsService,
                               CardIllustrationService cardIllustrationService
    ) {
        this.deckService = deckService;
        this.mediaService = mediaService;
        this.newsService = newsService;
        this.tagsService = tagsService;
        this.userService = userService;
        this.cardService = cardService;
        this.cardIllustrationService = cardIllustrationService;
    }

    @GetMapping("/twitch/streams")
    public List<Media> getStreams() {
        return mediaService.getStreams();
    }

    @GetMapping("/twitch/vods")
    public List<Media> getVods() {
        return mediaService.getVods();
    }

    @GetMapping("/twitch/check/{videoId}")
    public VideoCheck checkTwitchVideo(@PathVariable String videoId) {
        return mediaService.checkVideo(videoId);
    }


    @GetMapping("/user/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @GetMapping("/youtube/videos")
    public List<Media> getLastVideos() {
        return mediaService.getVideos();
    }

    @GetMapping("/media/content")
    public List<Media> getLastVodsAndVideos() {
        return mediaService.getVodsAndVideos();
    }

    @GetMapping("/news/latest/{number}")
    public List<News> getlastNews(@PathVariable String number) {
        return newsService.getLatestNews(Integer.parseInt(number));
    }

    @GetMapping("/news/latestIds/{number}")
    public List<News> getlastNewsIds(@PathVariable String number) {
        return newsService.getLatestNewsIds(Integer.parseInt(number));
    }

    @GetMapping("/news/{id}")
    public News getNews(@PathVariable String id) {
        return newsService.getNews(id);
    }


    // A etudier?
//    PageModule$PlainPageSerializationWarning : Serializing PageImpl instances as-is is not supported, meaning that there is no guarantee about the stability of the resulting JSON structure!
//    For a stable JSON structure, please use Spring Data's PagedModel (globally via @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO))
//    or Spring HATEOAS and Spring Data's PagedResourcesAssembler as documented in https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.pageables.


    // Passer ça en endpoint privé puisque c'est que les gens connecté pour le deckbuilder qui y accède?
    // Evite les abus?
    @PostMapping("/cards")
    public Page<CardDto> getCardPage(@RequestBody CardSearchForm form) {
        return cardService.fetchWithSpecs(form);
    }

    @PostMapping("/cards/byName")
    public Page<CardDto> getCardPageByName(@RequestBody CardSearchForm form) {
        return cardService.fetchByName(form);
    }

    @PostMapping("/decks")
    public Page<DeckDto> getDecks(@RequestBody DeckSearchForm form) {
        return deckService.findDecks(form);
    }

    @GetMapping("/decks/{deckId}/language/{language}/version/{version}/minorVersion/{minorVersion}")
    public DeckDto getDeck(@PathVariable String deckId, @PathVariable Language language, @PathVariable Integer version, @PathVariable Integer minorVersion) {
        return deckService.getDeck(deckId, version, minorVersion, language);
    }

    @GetMapping("/seo/decks/{deckId}/version/{version}/minorVersion/{minorVersion}")
    public DeckView getDeckForCrawlers(@PathVariable String deckId, @PathVariable Integer version, @PathVariable Integer minorVersion) {
        return deckService.getDeckForCrawlers(deckId, version, minorVersion);
    }

    @GetMapping("/decks/owners")
    public List<UserCount> getDeckOwners() {
        return deckService.loadDeckOwners();
    }

    @GetMapping("/cards/illustrations")
    public List<IllustrationNameEntity> loadAllIllustrations() {
        return cardIllustrationService.loadIllustrationsReferential();
    }

    @GetMapping("/cards/names/{language}")
    public List<CardView> loadCardNames(@PathVariable Language language) {
        return cardService.loadNamesByLanguage(language);
    }

    @GetMapping("/tags/language/{language}")
    public List<SimpleTagDto> getAllTags(@PathVariable Language language) {
        return tagsService.getTagsByLanguage(language);
    }
}
