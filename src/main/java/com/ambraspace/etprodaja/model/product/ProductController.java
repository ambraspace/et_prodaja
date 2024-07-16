package com.ambraspace.etprodaja.model.product;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class ProductController
{

	@Autowired
	private ProductService productService;


	@Operation(summary = "Get all products (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of products returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageProduct.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/products")
	public Page<Product> getProducts(
			@Parameter(description = "Search query", required = false)
			@RequestParam(name = "q", required = false) String query,
			@Parameter(description = "Should the comment field be included in the search", required = false)
			@RequestParam(name = "cm", defaultValue = "false") Boolean includeComments,
			@Parameter(description = "Limit products to specified Warehouse ID", required = false)
			@RequestParam(name = "w", required = false) Long warehouseId,
			@Parameter(description = "Limit to products having all of the specified Tag IDs", required = false)
			@RequestParam(name = "t", required = false) List<String> tags,
			@Parameter(description = "Limit products to specified Category ID", required = false)
			@RequestParam(name = "ct", required = false) Long categoryId,
			@ParameterObject @PageableDefault(sort = "name") Pageable pageable)
	{
		try
		{
			Page<Product> retVal =
			productService.getProducts(query, includeComments, warehouseId, tags, categoryId, pageable);
			return retVal;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get product", responses = {
			@ApiResponse(responseCode = "200", description = "Product returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Product.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/products/{id}")
	public Product getProduct(@PathVariable Long id)
	{
		try
		{
			return productService.getProduct(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new product", responses = {
			@ApiResponse(responseCode = "200", description = "Product added", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Product.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping(path = "/api/products")
	public Product addProduct(
			@RequestBody Product product)
	{
		try
		{
			return productService.addProduct(product);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update product", responses = {
			@ApiResponse(responseCode = "200", description = "Product updated", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Product.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping(path = "/api/products/{id}")
	public Product updateProduct(
			@PathVariable Long id,
			@RequestBody Product product)
	{
		try
		{
			return productService.updateProduct(id, product);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete product", responses = {
			@ApiResponse(responseCode = "200", description = "Product deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/products/{id}")
	public void deleteProduct(@PathVariable Long id)
	{
		try
		{
			productService.deleteProduct(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}


