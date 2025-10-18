package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.clients.Auth0Client;
import fr.psalles.kmdeckbuilder.commons.exceptions.UnauthorizedException;
import fr.psalles.kmdeckbuilder.models.extern.auth0.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Auth0Service {

    private final Auth0Client auth0Client;

    public Auth0Service(Auth0Client auth0Client) {
        this.auth0Client = auth0Client;
    }

    public void sendEmail(String userId) {
        String token = auth0Client.getBearerToken();
        try {
            String jobId = auth0Client.sendEmail(userId, token);
            log.info("Validation email pour {} : {}", userId, jobId);
        } catch (UnauthorizedException e) {
            // on suppose que c'est le token qui a expiré
            auth0Client.evictToken();
            auth0Client.getBearerToken();
            String jobId = auth0Client.sendEmail(userId, token);
            log.info("Validation email pour {} : {}", userId, jobId);
            // JobId pas utilisé mais dispo si besoin?
        }
    }

    public List<UserResponse.User> fetchUsers(boolean verified) {
        String token = auth0Client.getBearerToken();
        try {
            log.info("Chargement des users Auth0 verified : {}", verified);
            return auth0Client.fetchUsers(verified, token);
        } catch (UnauthorizedException e) {
            // on suppose que c'est le token qui a expiré
            auth0Client.evictToken();
            auth0Client.getBearerToken();
            return auth0Client.fetchUsers(verified, token);
        }
    }

    public UserResponse.User fetchUser(String userId) {
        String token = auth0Client.getBearerToken();
        try {
            log.info("get user Auth0 : {}", userId);
            return auth0Client.fetchUser(userId, token);
        } catch (UnauthorizedException e) {
            // on suppose que c'est le token qui a expiré
            auth0Client.evictToken();
            auth0Client.getBearerToken();
            return auth0Client.fetchUser(userId, token);
        }
    }

}
