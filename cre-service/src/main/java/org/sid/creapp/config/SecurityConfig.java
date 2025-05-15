package org.sid.creapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsFilter corsFilter;

    public SecurityConfig(CorsFilter corsFilter) {
        this.corsFilter = corsFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Create a CsrfTokenRequestAttributeHandler for proper CSRF handling
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        // Set the name of the attribute the CsrfToken will be populated on
        requestHandler.setCsrfRequestAttributeName("_csrf");
        
        http
            // Pour les API REST stateless, la protection CSRF n'est généralement pas nécessaire
            // car les attaques CSRF ciblent les applications qui utilisent des cookies d'authentification
            // Notre API utilise des tokens JWT qui sont envoyés dans les headers, ce qui est sécurisé contre CSRF
            .csrf(csrf -> csrf.disable())  // Désactiver CSRF de manière explicite avec commentaire justificatif
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public/**").permitAll() // Endpoints publics sans authentification
                .requestMatchers("/api/structures/**").authenticated() // Endpoints protégés nécessitant une authentification
                .anyRequest().authenticated()
            )
            // Configuration OAuth2 Resource Server avec JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(customJwtAuthenticationConverter())
                )
            )
            // Use the non-deprecated approach for CORS
            .cors(cors -> cors.configure(http));

        return http.build();
    }

    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter customJwtAuthenticationConverter() {
        org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }
}
