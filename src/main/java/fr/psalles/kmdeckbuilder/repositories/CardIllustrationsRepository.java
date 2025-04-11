package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.IllustrationNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CardIllustrationsRepository extends JpaRepository<IllustrationNameEntity, Integer>, JpaSpecificationExecutor<IllustrationNameEntity> {

}
