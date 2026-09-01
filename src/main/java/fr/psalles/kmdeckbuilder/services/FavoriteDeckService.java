package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.commons.exceptions.ForbiddenException;
import fr.psalles.kmdeckbuilder.models.entities.DeckEntity;
import fr.psalles.kmdeckbuilder.models.entities.FavoriteAssociation;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import fr.psalles.kmdeckbuilder.models.entities.embedded.FavoriteAssociationIdentity;
import fr.psalles.kmdeckbuilder.repositories.DeckRepository;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FavoriteDeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    public FavoriteDeckService(DeckRepository deckRepository, UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    public boolean addFavoriteDeck(String deckId) {
        String userId = UserService.getUserIdFromSecurityContext();
        DeckEntity deck = deckRepository.findLastVersionForDeckId(deckId);

        if (!deck.getUserId().getUserId().equals(userId)) {
            UserEntity user = userRepository.findByUserId(userId);

            FavoriteAssociation favoriteAssociation =
                    new FavoriteAssociation(new FavoriteAssociationIdentity(deckId, userId), LocalDateTime.now());
            user.getFavorites().add(favoriteAssociation);
            userRepository.save(user);

            deckRepository.addLikeOnDeck(deckId);

            return true;
        } else {
            throw new ForbiddenException("Impossible de like son propre deck");
        }
    }

    public boolean removeFavoriteDeck(String deckId) {
        String userId = UserService.getUserIdFromSecurityContext();
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
