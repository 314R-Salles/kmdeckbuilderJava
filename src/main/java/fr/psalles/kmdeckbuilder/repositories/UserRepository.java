package fr.psalles.kmdeckbuilder.repositories;

import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findByUsername(String username);
    UserEntity findByUserId(String userId);


    @Transactional
    @Modifying
    @Query(value = "update multiwork.km_user ku set ku.verified=true  WHERE ku.userId in (:userIds)", nativeQuery = true)
    void setVerified(List<String> userIds);

}
