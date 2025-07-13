package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.TagAssociation;
import fr.psalles.kmdeckbuilder.models.entities.embedded.TagAssociationIdentity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AssociationRepository extends JpaRepository<TagAssociation, TagAssociationIdentity> {


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from multiwork.km_association asso WHERE asso.deckId = :deckId and asso.version = :version", nativeQuery = true)
    void deleteByDeckEntity(String deckId, int version);

}
