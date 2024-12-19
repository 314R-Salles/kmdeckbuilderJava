package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


// Apparait inutilisé dans intellij mais parce que isAdmin est utilisé dans des annotations
@Component("principalPropertiesAccessor")
public class PrincipalPropertiesAccessor {

    @Autowired
    UserRepository userRepository;

// MARCHE PAS, coté AUTH0, j'arrive pas à ajouter le role dans le token avec des actions.
//    public boolean isAdmin() {
//        Authentication user = SecurityContextHolder.getContext().getAuthentication();
//        return ((Jwt) user.getPrincipal()).getClaims().values().stream().anyMatch("ROLE_ADMIN"::equals);
////        return user.getPrincipal();
//    }

    // appelle la bdd pour valider que l'utilisateur a Admin true.
    // ne doit pas produire de NPE puisque sur des endpoints qui requierent d'etre connecté
    // appeler la bdd au lieu de lire le token est plus couteux, mais les endpoints admins n'ont pas vocation a être spammés.
    public boolean isAdmin() {
        return userRepository.findByUserId(SecurityContextHolder.getContext().getAuthentication().getName()).isAdmin();
    }
}