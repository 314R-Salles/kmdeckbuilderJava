package fr.psalles.kmdeckbuilder.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("principalPropertiesAccessor")
public class PrincipalPropertiesAccessor {

    public boolean isAdmin() {
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        return ((Jwt) user.getPrincipal()).getClaims().values().stream().anyMatch("ROLE_ADMIN"::equals);
//        return user.getPrincipal();
    }
}