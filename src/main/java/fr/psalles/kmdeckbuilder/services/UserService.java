package fr.psalles.kmdeckbuilder.services;

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
    TwitchService twitchService;

    public User checkUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findById(userId);
        // si l'utilisateur n'existe pas, initialisé
        if (user.isEmpty()) {
            return new User(userRepository.save(UserEntity.builder().userId(userId).admin(false).lastLogin(LocalDateTime.now()).build()));
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
    public User updateUser(User updatedUser) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        user.setUsername(updatedUser.getUsername());
        user.setIconId(updatedUser.getIconId());
        return new User(userRepository.save(user));
    }

    public User linkTwitch(String token) {
        String username = this.twitchService.getUsernameFromToken(token);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        user.setTwitchUsername(username);
        return new User(userRepository.save(user));
    }

    public User removeAccount() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserId(userId);
        user.setTwitchUsername(null);
        return new User(userRepository.save(user));
    }


}
