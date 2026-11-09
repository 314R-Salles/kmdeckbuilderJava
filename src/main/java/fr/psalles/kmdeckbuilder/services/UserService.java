package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.commons.exceptions.BusinessException;
import fr.psalles.kmdeckbuilder.models.User;
import fr.psalles.kmdeckbuilder.models.UserView;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private static final long PASSWORD_REQUEST_COOLDOWN_HOURS = 1;

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
//    @PostConstruct
//    public void init() {
//        log.info("startup verified users");
//        List<String> users = auth0Service.fetchUsers(true).stream().map(UserResponse.User::getUser_id).toList();
//        userRepository.setVerified(users);
//    }

    public static String getUserIdFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public UserEntity getUserReferenceFromSecurityContext() {
        return userRepository.getReferenceById(getUserIdFromSecurityContext());
    }

    public static boolean isAuthenticatedInSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).noneMatch(a -> a.equals("ROLE_ANONYMOUS"));
    }


    public User checkUser() {
        String userId = getUserIdFromSecurityContext();
        Optional<UserEntity> user = userRepository.findById(userId);
        // si l'utilisateur n'existe pas, initialisé
        if (user.isEmpty()) {
            return new User(userRepository.save(UserEntity.builder().userId(userId).username("User" + userId.substring(6, 13)).admin(false).lastLogin(LocalDateTime.now()).build()));
        } else {
            user.get().setLastLogin(LocalDateTime.now());
            return new User(userRepository.save(user.get()));
        }
    }

    public UserView getUser(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        } else {
            return new UserView(user);
        }
    }

    // On ne save pas l'updatedUser, on extrait les données nécessaires.
    public User updateUser(User updatedUser) {
        String userId = getUserIdFromSecurityContext();
        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} met à jour son pseudo en {}", user.getUsername(), updatedUser.getUsername());

        UserEntity userWithTargetUsername = userRepository.findByUsername(updatedUser.getUsername());
        if (userWithTargetUsername != null && !userWithTargetUsername.getUserId().equals(userId)) {
            log.error("{} essaie de prendre le pseudo {} mais il est déjà utilisé", userId, updatedUser.getUsername());
            throw new BusinessException("Le pseudo est déjà pris");
        }

        user.setUsername(updatedUser.getUsername());
        return new User(userRepository.save(user));
    }

    public User refreshMailStatus() {
        String userId = getUserIdFromSecurityContext();
        boolean verified = auth0Service.fetchUser(userId).isEmail_verified();

        if (verified) {
            userRepository.setVerified(Collections.singletonList(userId));
        }

        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} is verified {} : ", user.getUserId(), verified);
        return new User(user);
    }

    public User sendValidationEmail() {
        String userId = getUserIdFromSecurityContext();
        auth0Service.sendEmail(userId);
        UserEntity user = userRepository.findByUserId(userId);

        LocalDateTime now = LocalDateTime.now();
        user.setLastValidationEmail(now);
        return new User(userRepository.save(user));
    }

    public User requestPasswordChange() {
        String userId = getUserIdFromSecurityContext();
        UserEntity user = userRepository.findByUserId(userId);
        LocalDateTime lastRequest = user.getLastPasswordChangeRequest();
        if (lastRequest != null && lastRequest.isAfter(LocalDateTime.now().minusHours(PASSWORD_REQUEST_COOLDOWN_HOURS))) {
            throw new BusinessException("Une demande de changement de mot de passe a déjà été envoyée, réessayez plus tard");
        }

        log.info("{} demande un mail de changement de mdp", userId);
        auth0Service.changePassword(userId);
        user.setLastPasswordChangeRequest(LocalDateTime.now());
        return new User(userRepository.save(user));
    }

    public User linkTwitch(String token) {
        String username = this.mediaService.getUsernameFromToken(token);
        String userId = getUserIdFromSecurityContext();
        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} associe son compte twitch {}", user.getUsername(), username);
        user.setTwitchUsername(username);
        return new User(userRepository.save(user));
    }

    public User removeAccount() {
        String userId = getUserIdFromSecurityContext();
        UserEntity user = userRepository.findByUserId(userId);
        log.info("{} dissocie son compte twitch", user.getUsername());
        user.setTwitchUsername(null);
        return new User(userRepository.save(user));
    }


}
