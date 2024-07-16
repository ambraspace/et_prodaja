package com.ambraspace.etprodaja.model.warehouse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class WarehouseController
{

	@Autowired
	private WarehouseService warehouseService;


	@Operation(summary = "Get warehouses by company", responses = {
			@ApiResponse(responseCode = "200", description = "List of warehouses returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema = @Schema(implementation = Warehouse.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/companies/{companyId}/warehouses")
	public List<Warehouse> getWarehouses(@PathVariable Long companyId)
	{
		try
		{
			return warehouseService.getWarehouses(companyId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get a warehouse by company ID and warehouse ID", responses = {
			@ApiResponse(responseCode = "200", description = "Warehouse returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Warehouse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/companies/{companyId}/warehouses/{id}")
	public Warehouse getWarehouse(@PathVariable Long companyId, @PathVariable Long id)
	{
		try
		{
			return warehouseService.getWarehouse(companyId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new warehouse",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(examples = {
							@ExampleObject(value = """
{
  "name":"string"
}
									""")
					})),
			responses = {
			@ApiResponse(responseCode = "200", description = "Warehouse added", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Warehouse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/companies/{companyId}/warehouses")
	public Warehouse addWarehouse(@PathVariable Long companyId, @RequestBody Warehouse warehouse)
	{
		try
		{
			return warehouseService.addWarehouse(companyId, warehouse);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update warehouse",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(examples = {
							@ExampleObject(value = """
{
  "name":"string"
}
									""")
					})),
			responses = {
			@ApiResponse(responseCode = "200", description = "Warehouse updated (updated object returned)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Warehouse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/companies/{companyId}/warehouses/{id}")
	public Warehouse updateWarehouse(@PathVariable Long companyId, @PathVariable Long id, @RequestBody Warehouse warehouse)
	{
		try
		{
			return warehouseService.updateWarehouse(companyId, id, warehouse);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete warehouse", responses = {
			@ApiResponse(responseCode = "200", description = "Warehouse deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/companies/{companyId}/warehouses/{id}")
	public void deleteWarehouse(@PathVariable Long companyId, @PathVariable Long id)
	{
		try
		{
			warehouseService.deleteWarehouse(companyId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}



	@Operation(summary = "Search warehouses by query", responses = {
			@ApiResponse(responseCode = "200", description = "List of warehouses returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema = @Schema(implementation = Warehouse.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/warehouses/search")
	public List<Warehouse> searchWarehouses(
			@RequestParam("q") String query,
			@RequestParam("size") Integer size
	)
	{
		try
		{
			return warehouseService.searchWarehouses(query, size);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
