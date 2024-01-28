package com.ambraspace.etprodaja.auth;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ambraspace.auth.jwt.JwtRequest;
import com.ambraspace.auth.jwt.JwtTokenUtil;
import com.ambraspace.etprodaja.model.user.User;
import com.ambraspace.etprodaja.model.user.UserService;
import com.ambraspace.etprodaja.util.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtUtil;


	@Operation(summary = "Authenticate here and get JWT", responses = {
			@ApiResponse(responseCode = "200", description = "JWT returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = CustomJwtResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
    @PostMapping("/authenticate")
    public CustomJwtResponse authenticate(@RequestBody JwtRequest jwtRequest)
            throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    jwtRequest.getUsername(), jwtRequest.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (LockedException e) {
            throw new Exception("USER_ACCOUNT_LOCKED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        User user = userService.getUser(jwtRequest.getUsername());

        String token = jwtUtil.generateToken(user.getUsername());

        return new CustomJwtResponse(token, user,
                jwtUtil.getExpiration(token).toInstant().atZone(ZoneId.systemDefault()));

    }


	@Operation(summary = "Get new JWT after the expiration", responses = {
			@ApiResponse(responseCode = "200", description = "Refreshed JWT returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = CustomJwtResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
    @GetMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {

        String username = null;
        try
        {
            username = jwtUtil.getUsernameFromToken(jwtUtil.extractJwtFromRequest(request));
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        User user = userService.getUser(username);
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new CustomJwtResponse(token, user,
                jwtUtil.getExpiration(token).toInstant().atZone(ZoneId.systemDefault())));

    }


}