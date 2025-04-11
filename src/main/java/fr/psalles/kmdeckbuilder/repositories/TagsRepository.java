package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagsRepository extends JpaRepository<TagEntity, String> {

    List<TagEntity> findByDisabled(boolean disabled);

    @Query("SELECT COALESCE(MAX(e.tagIdentity.id),0) FROM TagEntity e")
    Integer findMaxId();
}
