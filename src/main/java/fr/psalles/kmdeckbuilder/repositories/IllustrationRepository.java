package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.NewsIllustrationEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.IllustrationView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IllustrationRepository extends JpaRepository<NewsIllustrationEntity, String> {

    List<IllustrationView> findAllProjectedBy();

}
