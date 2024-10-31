package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    PrincipalPropertiesAccessor principalPropertiesAccessor;

    public User checkUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = principalPropertiesAccessor.isAdmin();
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return new User(userRepository.save(UserEntity.builder().id(userId).lastLogin(LocalDateTime.now()).build()), isAdmin);
        } else {
            user.get().setLastLogin(LocalDateTime.now());
            return new User(userRepository.save(user.get()), isAdmin);
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

    public User updateUser(User updatedUser) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
        user.setUsername(updatedUser.getUsername());
        user.setIconId(updatedUser.getIconId());
        return new User(userRepository.save(user), principalPropertiesAccessor.isAdmin());
    }

    public User linkTwitch(String token) {
        String username = this.twitchService.getUsernameFromToken(token);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
        user.setTwitchUsername(username);
        return new User(userRepository.save(user), principalPropertiesAccessor.isAdmin());
    }

    public User removeAccount() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici
        user.setTwitchUsername(null);
        return new User(userRepository.save(user), principalPropertiesAccessor.isAdmin());
    }


}
