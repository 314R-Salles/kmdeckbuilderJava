package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.DeckDto;
import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.requests.DeckSearchForm;
import fr.psalles.kmdeckbuilder.models.responses.SavedDeckResponse;
import fr.psalles.kmdeckbuilder.services.DeckService;
import fr.psalles.kmdeckbuilder.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class LoggedUserApiController {

    private final UserService userService;
    private final DeckService deckService;


    @Autowired
    public LoggedUserApiController(UserService userService,
                                   DeckService deckService
    ) {
        this.userService = userService;
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


    @GetMapping("/user/favorite/add/{deckId}")
    public boolean addFavoriteDeck(@PathVariable String deckId) {
        return deckService.addFavoriteDeck(deckId);
    }

    @GetMapping("/user/favorite/remove/{deckId}")
    public boolean removeFavoriteDeck(@PathVariable String deckId) {
        return deckService.removeFavoriteDeck(deckId);
    }

    @PostMapping("/deck")
    public SavedDeckResponse saveDeck(@RequestBody DeckCreateForm form) {
        return deckService.saveDeck(form);
    }

    @PostMapping("/decks")
    public Page<DeckDto> getDecks(@RequestBody DeckSearchForm form) {
        return deckService.findDecks(form, true);
    }

    @PostMapping("/decks/recentFavorites")
    public Page<DeckDto> getFavoriteDecks() {
        return deckService.findFavoriteDecks();
    }


}
