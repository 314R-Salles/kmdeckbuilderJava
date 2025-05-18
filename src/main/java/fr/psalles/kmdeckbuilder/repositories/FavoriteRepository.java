package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.FavoriteAssociation;
import fr.psalles.kmdeckbuilder.models.entities.projections.FavoriteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<FavoriteAssociation, String> {

    @Query(value = "select deckId, count(*) as count " +
            "from multiwork.km_favorite_association where deckId in (:deckIds) group by deckId",
            nativeQuery = true)
    List<FavoriteCount> countFavorites(List<String> deckIds);
}
