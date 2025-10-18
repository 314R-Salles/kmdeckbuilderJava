package fr.psalles.kmdeckbuilder.clients;

import fr.psalles.kmdeckbuilder.commons.client.BaseHttpClient;
import fr.psalles.kmdeckbuilder.models.extern.TokenResponse;
import fr.psalles.kmdeckbuilder.models.extern.auth0.JobResponse;
import fr.psalles.kmdeckbuilder.models.extern.auth0.MailRequest;
import fr.psalles.kmdeckbuilder.models.extern.auth0.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Auth0Client {

    @Autowired
    private BaseHttpClient client;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.domain}")
    private String domain;

    @Cacheable("auth0_token")
    public String getBearerToken() {
        log.info("Api call : Init Auth 0");
        String url = "https://" + domain + "/oauth/token";
        String request = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret + "&audience=https%3A%2F%2F" + domain + "%2Fapi%2Fv2%2F";
        return client.makeCall(HttpMethod.POST, url, TokenResponse.class, request,
                getUrlEncodedHeaders()).getAccess_token();
    }

    public String sendEmail(String userId, String token) {
        MailRequest request = new MailRequest(userId);
        String url = "https://" + domain + "/api/v2/jobs/verification-email";
        return client.makeCall(HttpMethod.POST, url, JobResponse.class, request,
                getAuthHeaders(token)).getId();
    }

    public List<UserResponse.User> fetchUsers(boolean verified, String token) {
        String url = "https://" + domain + "/api/v2/users?page=0&per_page=100&include_totals=true&fields=user_id,email_verified&include_fields=true&q=email_verified=" + verified;
        UserResponse response = client.makeCall(HttpMethod.GET, url, UserResponse.class, null,
                getAuthHeaders(token));
        log.info("Il y a {} users avec verified {}", response.getTotal(), verified);
        return response.getUsers();
    }

    public UserResponse.User fetchUser(String userId, String token) {
        // include_totals=true n'apporte pas d'info mais si on l'enlève, la réponse n'est pas au format "users : []" mais directement []
        // Donc on le met pour avoir le même format de réponse que fetchUsers
        String url = "https://" + domain + "/api/v2/users?include_totals=true&fields=user_id,email_verified&include_fields=true&q=user_id=" + userId;
        UserResponse response = client.makeCall(HttpMethod.GET, url, UserResponse.class, null,
                getAuthHeaders(token));
        return response.getUsers().getFirst(); // on peut pas faire un appel auth0 sans être connecté donc exister
    }


    private HttpHeaders getUrlEncodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private HttpHeaders getAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    @CacheEvict("auth0_token")
    public void evictToken() {
    }

}
