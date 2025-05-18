package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.DeckEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    @Query(value = "select kd.* from multiwork.km_deck kd " +
            " INNER JOIN multiwork.km_last_version v " +
            "on (kd.deckId = v.deckId and kd.version = v.version) WHERE v.deckId = :id",
            nativeQuery = true)
    DeckEntity findlastVersionForDeckId(String id);

}
