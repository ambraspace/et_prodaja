package com.ambraspace.etprodaja.model.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class CategoryController
{

	@Autowired
	private CategoryService categoryService;


	@Operation(summary = "Get all categories", responses = {
			@ApiResponse(responseCode = "200", description = "List of categories returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema = @Schema(implementation = Category.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
//	@SecurityRequirement(name = "JWT")
//	@RolesAllowed({"ADMIN", "USER", "CUSTOMER"})
	@GetMapping("/api/categories")
	public List<Category> getCategories()
	{
		try
		{
			return categoryService.getCategories();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Save all categories", responses = {
			@ApiResponse(responseCode = "200", description = "Saved list of categories returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema = @Schema(implementation = Category.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/categories")
	public List<Category> saveCategories(@RequestBody List<Category> categories)
	{
		try
		{
			return categoryService.saveCategories(categories);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
