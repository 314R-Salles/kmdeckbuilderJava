package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.Illustration;
import fr.psalles.kmdeckbuilder.models.News;
import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.entities.projections.IllustrationView;
import fr.psalles.kmdeckbuilder.services.DeckService;
import fr.psalles.kmdeckbuilder.services.NewsService;
import fr.psalles.kmdeckbuilder.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class LoggedUserApiController {

    private final UserService userService;
    private final NewsService newsService;
    private final DeckService deckService;


    @Autowired
    public LoggedUserApiController(UserService userService,
                                   NewsService newsService,
                                   DeckService deckService
    ) {
        this.userService = userService;
        this.newsService = newsService;
        this.deckService = deckService;
    }

    @GetMapping("/user")
    public User getUser() {
        return userService.checkUser();
    }

    // (Bon... il faudrait https://stackoverflow.com/a/77035670 pour autoriser PUT)
    @PostMapping("/user")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/user/twitch/linkAccount")
    public User linkAccount(@RequestHeader("twitch") String token) {
        return userService.linkTwitch(token);
    }

    @GetMapping("/user/twitch/remove")
    public User removeAccount() {
        return userService.removeAccount();
    }


    @PostMapping("/deck")
    public Long saveDeck(@RequestBody DeckCreateForm form) {
        return deckService.saveDeck(form);
    }

}
