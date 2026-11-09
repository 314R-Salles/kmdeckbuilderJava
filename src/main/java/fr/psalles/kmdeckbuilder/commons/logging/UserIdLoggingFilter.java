package fr.psalles.kmdeckbuilder.commons.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Ajoute l'id de l'utilisateur connecté à toutes les lignes de log de la requête.
// Ne pas déclarer en @Component : Spring Boot l'exécuterait aussi hors de la chaîne de sécurité.
public class UserIdLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_KEY = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            MDC.put(MDC_KEY, authentication.getName());
        }
        try {
            chain.doFilter(request, response);
        } finally {
            // Les threads Tomcat sont réutilisés : sans ça, l'id passerait à la requête suivante
            MDC.remove(MDC_KEY);
        }
    }
}
