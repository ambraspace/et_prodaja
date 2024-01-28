package com.ambraspace.etprodaja.model.user;

import java.security.Principal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class UserController
{

	@Autowired
	private UserService userService;


	@Operation(summary = "Get all users (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of users returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageUser.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/users")
	public Page<User> getUsers(@ParameterObject @PageableDefault(sort = "username") Pageable pageable)
	{
		try
		{
			return userService.getUsers(pageable);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get authenticated user", responses = {
			@ApiResponse(responseCode = "200", description = "Returns User object if authenticated or null otherwise", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = User.class))
			})
	})
	@SecurityRequirement(name = "JWT")
    @GetMapping("/user")
    public User getUser(Principal user)
    {
		if (user != null)
			return userService.getUser(user.getName());
		else
			return null;
    }


	@Operation(summary = "Get user by username", responses = {
			@ApiResponse(responseCode = "200", description = "User returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = User.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/users/{username}")
	public User getUser(@PathVariable String username)
	{
		try
		{
			return userService.getUser(username);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new user", responses = {
			@ApiResponse(responseCode = "200", description = "User added", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = User.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@PostMapping("/api/users")
	public User addUser(@RequestBody User user)
	{
		try
		{
			return userService.addUser(user);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update user", responses = {
			@ApiResponse(responseCode = "200", description = "User updated (updated object returned)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = User.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@PutMapping("/api/users/{username}")
	public User updateUser(@PathVariable String username, @RequestBody User user)
	{
		try
		{
			return userService.updateUser(username, user);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete user by username", responses = {
			@ApiResponse(responseCode = "200", description = "User deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@DeleteMapping("/api/users/{username}")
	public void deleteUser(@PathVariable String username)
	{
		try
		{
			userService.deleteUser(username);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
