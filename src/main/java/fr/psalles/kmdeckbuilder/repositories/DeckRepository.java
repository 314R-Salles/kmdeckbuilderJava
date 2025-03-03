package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.DeckEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.UserCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeckRepository extends JpaRepository<DeckEntity, String>, JpaSpecificationExecutor<DeckEntity> {

    @Query(value = "select username, count(*) as count " +
            "from multiwork.km_deck kd left join multiwork.km_user ku on kd.userId = ku.userId group by  username",
            nativeQuery = true)
    List<UserCount> findAllProjectedBy();

}
