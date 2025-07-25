package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.DeckEntity;
import fr.psalles.kmdeckbuilder.models.entities.embedded.DeckIdentity;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeckRepository extends JpaRepository<DeckEntity, String>, JpaSpecificationExecutor<DeckEntity> {

    @Query(value =
            "select ku.username, T1.count from (select d.userId as userId,  count(*) as count\n" +
                    "               from multiwork.km_deck d\n" +
                    "                        INNER JOIN multiwork.km_last_version v on (d.deckId = v.deckId and d.version = v.version)\n" +
                    "               group by d.userId) as T1 LEFT JOIN  multiwork.km_user ku on T1.userId = ku.userId ",
            nativeQuery = true)
    List<UserCount> countOwners();

    DeckEntity findById(DeckIdentity identity);

    @Query(value = "select * from multiwork.km_deck kd  WHERE kd.deckId = :id and kd.version = " +
            "(SELECT MAX(version) FROM multiwork.km_deck where deckId = :id)",
            nativeQuery = true)
    DeckEntity findLastVersionForDeckId(String id);


    // Le modèle de données est pas clean avec le systeme de versions. Donc on met à jour le compteur sur toutes les versions
    @Transactional
    @Modifying
    @Query(value = "update multiwork.km_deck kd set kd.favoriteCount =kd.favoriteCount+1  WHERE kd.deckId = :id", nativeQuery = true)
    void addLikeOnDeck(String id);

    @Transactional
    @Modifying
    @Query(value = "update multiwork.km_deck kd set kd.favoriteCount =kd.favoriteCount-1  WHERE kd.deckId = :id", nativeQuery = true)
    void removeLikeOnDeck(String id);


    // A utiliser dans un CRON tous les weekends
    @Transactional
    @Modifying
    @Query(value = "UPDATE km_deck kd " +
            " JOIN ( SELECT deckId, COUNT(*) AS cnt FROM km_favorite_association GROUP BY deckId ) AS favCounts ON kd.deckId = favCounts.deckId " +
            " SET kd.favoriteCount = favCounts.cnt;", nativeQuery = true)
    void initLikesOnDecks();



    @Query(value = "select version from multiwork.km_deck kd  WHERE kd.deckId = :id",
            nativeQuery = true)
    List<Integer> findVersionNumberForDeckId(String id);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from multiwork.km_deck kd WHERE kd.deckId = :deckId and kd.version = :version", nativeQuery = true)
    void deleteByDeckEntity(String deckId, int version);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from multiwork.km_deck kd WHERE kd.deckId = :deckId", nativeQuery = true)
    void deleteByDeckEntity(String deckId);


}
