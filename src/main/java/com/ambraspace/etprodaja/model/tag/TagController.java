package com.ambraspace.etprodaja.model.tag;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class TagController
{

	@Autowired
	private TagService tagService;


	@Operation(summary = "Get all product tags (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of tags returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageTag.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/tags")
	public Page<Tag> getTags(@ParameterObject @PageableDefault(sort = "name") Pageable pageable)
	{
		try
		{
			return tagService.getTags(pageable);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get product tag by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Product tag returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Tag.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/tags/{id}")
	public Tag getTag(@PathVariable Long id)
	{
		try
		{
			return tagService.getTag(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new product tag", responses = {
			@ApiResponse(responseCode = "200", description = "Product tag added", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Tag.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@PostMapping("/api/tags")
	public Tag addTag(@RequestBody Tag tag)
	{
		try
		{
			return tagService.addTag(tag);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete product tag", responses = {
			@ApiResponse(responseCode = "200", description = "Product tag deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@DeleteMapping("/api/tags/{id}")
	public void deleteTag(@PathVariable Long id)
	{
		try
		{
			tagService.deleteTag(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Search product tags", responses = {
			@ApiResponse(responseCode = "200", description = "List of tags returned (max. 10)", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema = @Schema(implementation = Tag.class)
					))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/tags/search")
	public List<Tag> searchTags(@RequestParam(required = true, name = "q") String query)
	{
		try
		{
			return tagService.serachTags(query);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
