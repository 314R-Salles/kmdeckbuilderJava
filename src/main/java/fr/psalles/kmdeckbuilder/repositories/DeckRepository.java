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

//    @Query(value = "select * from multiwork.km_deck kd  WHERE kd.deckId = :id and kd.version = " +
//            "(SELECT MAX(version) FROM multiwork.km_last_version v where deckId = :id)",
//            nativeQuery = true)
//    DeckEntity findLastVersionForDeckId(String id);


    @Query(value = "select * from multiwork.km_deck kd  WHERE kd.deckId = :id and kd.version = " +
            "(SELECT MAX(version) FROM multiwork.km_deck where deckId = :id)",
            nativeQuery = true)
    DeckEntity findLastVersionForDeckId(String id);


    @Query(value = "select version from multiwork.km_deck kd  WHERE kd.deckId = :id",
            nativeQuery = true)
    List<Integer> findVersionNumberForDeckId(String id);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from multiwork.km_deck kd WHERE kd.deckId = :deckId and kd.version = :version", nativeQuery = true)
    void deleteByDeckEntity(String deckId, int version);


}
