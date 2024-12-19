package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findByUsername(String username);
    UserEntity findByUserId(String userId);

}
