package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.commons.exceptions.BusinessException;
import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MediaService mediaService;

    public User checkUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findById(userId);
        // si l'utilisateur n'existe pas, initialisé
        if (user.isEmpty()) {
            return new User(userRepository.save(UserEntity.builder().userId(userId).username("User" + userId.substring(6, 13)).admin(false).lastLogin(LocalDateTime.now()).build()));
        } else {
            user.get().setLastLogin(LocalDateTime.now());
            return new User(userRepository.save(user.get()));
        }
    }

    public User getUser(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        } else {
            return User.builder()
                    .username(user.getUsername())
                    .twitchUsername(user.getTwitchUsername())
                    .iconId(user.getIconId())
                    .lastLogin(user.getLastLogin().toString()) // toString )=> formatter
                    .build();
        }
    }

    // findByUserId au lieu de findById pour avoir une méthode qui renvoie pas un Optional

    // On ne save pas l'updatedUser, on extrait les données nécessaires.
    // On ne risque pas qu'un petit malin s'update son statut Admin
    // Enfin bon, la vraie bonne pratique c'est faire un DTO
    public User updateUser(User updatedUser) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} met à jour son pseudo en {}", user.getUsername(), updatedUser.getUsername());

        UserEntity userWithTargetUsername = userRepository.findByUsername(updatedUser.getUsername());
        if (userWithTargetUsername != null && !userWithTargetUsername.getUserId().equals(userId)) {
            log.error("{} essaie de prendre le pseudo {} mais il est déjà utilisé", userId, updatedUser.getUsername());
            throw new BusinessException("Le pseudo est déjà pris");
        }

        user.setUsername(updatedUser.getUsername());
        user.setIconId(updatedUser.getIconId());
        return new User(userRepository.save(user));
    }

    public User linkTwitch(String token) {
        String username = this.mediaService.getUsernameFromToken(token);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} associe son compte twitch {}", user.getUsername(), username);
        user.setTwitchUsername(username);
        return new User(userRepository.save(user));
    }

    public User removeAccount() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} dissocie son compte twitch", user.getUsername());
        user.setTwitchUsername(null);
        return new User(userRepository.save(user));
    }


}
