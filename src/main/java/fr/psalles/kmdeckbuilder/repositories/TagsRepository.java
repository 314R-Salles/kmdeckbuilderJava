package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.TagEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.TagCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagsRepository extends JpaRepository<TagEntity, String> {

    List<TagEntity> findByTagIdentityLanguageAndDisabled(Language language, boolean disabled);

    @Query("SELECT COALESCE(MAX(e.tagIdentity.id),0) FROM TagEntity e")
    Integer findMaxId();


    @Query(value = "select tagId, count(*) as count " +
            "from multiwork.km_tag_association t INNER JOIN multiwork.km_last_version v " +
            "on (t.deckId = v.deckId and t.version = v.version) group by t.tagId",
            nativeQuery = true)
    List<TagCount> countTags();
}
