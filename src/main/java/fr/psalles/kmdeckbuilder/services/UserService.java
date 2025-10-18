package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.commons.exceptions.BusinessException;
import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import fr.psalles.kmdeckbuilder.models.extern.auth0.UserResponse;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final MediaService mediaService;

    private final Auth0Service auth0Service;

    public UserService(UserRepository userRepository,
                       MediaService mediaService,
                       Auth0Service auth0Service) {
        this.userRepository = userRepository;
        this.mediaService = mediaService;
        this.auth0Service = auth0Service;
    }


    // Au lancement de l'appli, pour synchro les users vérifiés chez Auth0 et notre bdd. (Api ne marche jusqu'à 1000 résultats)
    // n'est pas codé pour chainer sur plusieurs pages (ne récupère que 100)
    @PostConstruct
    public void init() {
        log.info("startup verified users");
        List<String> users = auth0Service.fetchUsers(true).stream().map(UserResponse.User::getUser_id).toList();
        userRepository.setVerified(users);
    }

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

    public User refreshMailStatus() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean verified = auth0Service.fetchUser(userId).isEmail_verified();

        if (verified) {
            userRepository.setVerified(Collections.singletonList(userId));
        }

        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} is verified {} : ", user.getUserId(), verified);
        return new User(user);
    }

    public User sendValidationEmail() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        auth0Service.sendEmail(userId);
        UserEntity user = userRepository.findByUserId(userId);

        LocalDateTime now = LocalDateTime.now();
        user.setLastValidationEmail(now);
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
