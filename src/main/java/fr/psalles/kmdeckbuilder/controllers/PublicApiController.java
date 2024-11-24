package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.*;
import fr.psalles.kmdeckbuilder.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public PublicApiController(TwitchService twitchService, YoutubeService youtubeService, NewsService newsService, UserService userService, CardService cardService) {
        this.twitchService = twitchService;
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
    public List<SearchResultDto> getLastVideos() {
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


    @PostMapping("/cards")
    public List<CardDto> getCardPage(@RequestBody CardSearchForm form) {
        return cardService.fetchWithSpecs(form).getContent();
    }

}
