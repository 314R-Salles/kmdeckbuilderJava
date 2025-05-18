 package fr.psalles.kmdeckbuilder.repositories;

 import fr.psalles.kmdeckbuilder.models.entities.LastVersionEntity;
 import org.springframework.data.jpa.repository.JpaRepository;

 public interface LastVersionRepository extends JpaRepository<LastVersionEntity, String> {


 }
