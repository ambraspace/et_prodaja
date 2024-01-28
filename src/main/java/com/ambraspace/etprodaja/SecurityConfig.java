package com.ambraspace.etprodaja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.bind.annotation.RestController;

import com.ambraspace.auth.jwt.JwtAuthenticationEntryPoint;
import com.ambraspace.auth.jwt.JwtRequestFilter;


@EnableWebSecurity
@RestController
@Configuration
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
public class SecurityConfig
{


    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userService) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {

        http
            .authorizeHttpRequests(request ->
                request
                    .requestMatchers("/authenticate", "/user", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated())
            .exceptionHandling(exception ->
                exception
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session ->
                session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .logout(logout ->
                logout
                    .logoutSuccessHandler((request, response, authentication) -> {}))
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .csrf(c -> c
            	.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            	.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));

            http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();

    }


}



