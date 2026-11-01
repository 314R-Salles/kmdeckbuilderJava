package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.DeckDto;
import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.models.requests.DeckCreateForm;
import fr.psalles.kmdeckbuilder.models.responses.SavedDeckResponse;
import fr.psalles.kmdeckbuilder.services.DeckService;
import fr.psalles.kmdeckbuilder.services.FavoriteDeckService;
import fr.psalles.kmdeckbuilder.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class LoggedUserApiController {

    private final UserService userService;
    private final DeckService deckService;
    private final FavoriteDeckService favoriteDeckService;

    public LoggedUserApiController(UserService userService,
                                   DeckService deckService,
                                   FavoriteDeckService favoriteDeckService
    ) {
        this.userService = userService;
        this.deckService = deckService;
        this.favoriteDeckService = favoriteDeckService;
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

    @PostMapping("/user/email")
    public User sendEmail() {
        return userService.sendValidationEmail();
    }

    @PostMapping("/user/email/refresh")
    public User refreshMailStatus() {
        return userService.refreshMailStatus();
    }

    @PostMapping("/user/password")
    public User changePassword() {
        return userService.requestPasswordChange();
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
        return favoriteDeckService.addFavoriteDeck(deckId);
    }

    @GetMapping("/user/favorite/remove/{deckId}")
    public boolean removeFavoriteDeck(@PathVariable String deckId) {
        return favoriteDeckService.removeFavoriteDeck(deckId);
    }

    @PostMapping("/deck/{deckId}")
    public boolean deleteDeck(@PathVariable String deckId) { return deckService.deleteDeck(deckId);}

    @PostMapping("/deck")
    public SavedDeckResponse saveDeck(@RequestBody @Valid DeckCreateForm form) {
        return deckService.saveDeck(form);
    }

    @PostMapping("/decks/recentFavorites/{language}")
    public Page<DeckDto> getFavoriteDecks(@PathVariable Language language) {
        return deckService.findFavoriteDecks(language);
    }

}
