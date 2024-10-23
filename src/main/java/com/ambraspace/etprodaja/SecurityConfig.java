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
                    .requestMatchers(
                    		"/api/authenticate",
                    		"/api/images/**",
                    		"/swagger-ui/**",
                    		"/v3/api-docs/**",
                    		"/error",
                    		"/api/categories",
                    		"/api/products",
                    		"/api/tags/search"
            		).permitAll()
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
            .csrf(c -> c.disable())
        	.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();

    }


//    final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
//    	private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();
//
//    	@Override
//    	public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
//    		/*
//    		 * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
//    		 * the CsrfToken when it is rendered in the response body.
//    		 */
//    		this.delegate.handle(request, response, csrfToken);
//    	}
//
//    	@Override
//    	public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
//    		/*
//    		 * If the request contains a request header, use CsrfTokenRequestAttributeHandler
//    		 * to resolve the CsrfToken. This applies when a single-page application includes
//    		 * the header value automatically, which was obtained via a cookie containing the
//    		 * raw CsrfToken.
//    		 */
//    		if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
//    			return super.resolveCsrfTokenValue(request, csrfToken);
//    		}
//    		/*
//    		 * In all other cases (e.g. if the request contains a request parameter), use
//    		 * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
//    		 * when a server-side rendered form includes the _csrf request parameter as a
//    		 * hidden input.
//    		 */
//    		return this.delegate.resolveCsrfTokenValue(request, csrfToken);
//    	}
//    }
//
//
//
//    final class CsrfCookieFilter extends OncePerRequestFilter {
//
//    	@Override
//    	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//    			throws ServletException, IOException {
//    		CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
//    		// Render the token value to a cookie by causing the deferred token to be loaded
//    		csrfToken.getToken();
//
//    		filterChain.doFilter(request, response);
//    	}
//    }


}



