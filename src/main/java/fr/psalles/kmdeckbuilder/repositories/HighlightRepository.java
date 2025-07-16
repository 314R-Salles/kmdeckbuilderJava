package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.DeckHighlight;
import fr.psalles.kmdeckbuilder.models.entities.embedded.AssociationIdentity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HighlightRepository extends JpaRepository<DeckHighlight, AssociationIdentity> {


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from multiwork.km_deck_highlight dh WHERE dh.deckId = :deckId and dh.version = :version", nativeQuery = true)
    void deleteByDeckEntity(String deckId, int version);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from multiwork.km_deck_highlight dh WHERE dh.deckId = :deckId", nativeQuery = true)
    void deleteByDeckEntity(String deckId);

}
