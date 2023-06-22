package org.curso.springcloud.msvc.users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

//@SuppressWarnings("ALL")
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository
    ) throws Exception {
        http
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/authorized", "/login").permitAll()
                            .requestMatchers(HttpMethod.GET, "/", "/{id}").hasAnyAuthority("SCOPE_read", "SCOPE_write")
                            .requestMatchers(HttpMethod.POST, "/").hasAuthority("SCOPE_write")
                            .requestMatchers(HttpMethod.PUT, "/{id}").hasAuthority("SCOPE_write")
                            .requestMatchers(HttpMethod.DELETE, "/{id}").hasAuthority("SCOPE_write")
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/msvc-users-client"))
                .oauth2Login(oauth2login -> oauth2login.loginPage("/oauth2/authorization/msvc-users-client"))
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(configurer -> configurer.jwt(withDefaults()))
                .csrf(AbstractHttpConfigurer::disable);
//                .logout(logout ->
//                        logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)));

        return http.build();
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler(
            ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logout");
        return logoutSuccessHandler;
    }
}
