package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.CardEntity;
import fr.psalles.kmdeckbuilder.models.entities.embedded.Identity;
import fr.psalles.kmdeckbuilder.models.entities.projections.CardView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, String>, JpaSpecificationExecutor<CardEntity> {

    List<CardEntity> findByCardIdentityIsIn(List<Identity> cardIdentityList);

    @Query(value =
            "select card.id, card.name from multiwork.km_card card where card.language = :language",
            nativeQuery = true)
    List<CardView> getAllNamesByLanguage(String language);
}
