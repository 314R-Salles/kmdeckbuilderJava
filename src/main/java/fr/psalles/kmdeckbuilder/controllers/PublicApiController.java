package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.*;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.requests.CardSearchForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.models.responses.AggregatedStream;
import fr.psalles.kmdeckbuilder.models.responses.AggregatedVod;
import fr.psalles.kmdeckbuilder.models.responses.YoutubeSearchResultDto;
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

    private final TwitchService twitchService;
    private final YoutubeService youtubeService;
    private final NewsService newsService;
    private final UserService userService;
    private final CardService cardService;
    private final DeckService deckService;

    @Autowired
    public PublicApiController(TwitchService twitchService,
                               DeckService deckService,
                               YoutubeService youtubeService, NewsService newsService, UserService userService, CardService cardService) {
        this.twitchService = twitchService;
        this.deckService = deckService;
        this.youtubeService = youtubeService;
        this.newsService = newsService;
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping("/twitch/streams")
    public List<AggregatedStream> getStreams() {
        return twitchService.getStreams();
    }

    @GetMapping("/twitch/vods")
    public List<AggregatedVod> getVods() {
        return twitchService.getVods();
    }


    @GetMapping("/user/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @GetMapping("/youtube/videos")
    public List<YoutubeSearchResultDto> getLastVideos() {
        return youtubeService.getVideos();
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

    @GetMapping("/decks/{deckId}/language/{language}")
    public DeckDto getDeck(@PathVariable String deckId, @PathVariable Language language) {
        return deckService.getDeck(deckId, language);
    }

    @GetMapping("/decks/owners")
    public List<UserCount> getDeck() {
        return deckService.loadDeckOwners();
    }

}
