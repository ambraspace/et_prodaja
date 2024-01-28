package com.ambraspace.etprodaja.auth;

import java.time.ZonedDateTime;

import com.ambraspace.auth.jwt.JwtResponse;
import com.ambraspace.etprodaja.model.user.User;

public class CustomJwtResponse extends JwtResponse {

    private static final long serialVersionUID = 1L;

    private final User.Role role;

    public CustomJwtResponse(String jwttoken, User user, ZonedDateTime exp) {
    	super(jwttoken, user.getUsername(), exp);
        this.role = user.getRole();
    }

    public User.Role getRole()
    {
        return this.role;
    }

}