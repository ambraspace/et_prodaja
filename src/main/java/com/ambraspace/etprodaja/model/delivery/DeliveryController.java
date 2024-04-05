package com.ambraspace.etprodaja.model.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.ambraspace.etprodaja.model.delivery.Delivery.Status;
import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class DeliveryController
{

	@Autowired
	private DeliveryService deliveryService;


	@Operation(summary = "Get delivery by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Delivery returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Delivery.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/deliveries/{id}")
	public Delivery getDelivery(@PathVariable("id") Long id)
	{
		return deliveryService.getDelivery(id);
	}


	@Operation(summary = "Get deliveries (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of deliveries returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageDelivery.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/deliveries")
	public Page<Delivery> getDeliveries(
			@Parameter(description = "Company ID", required = false)
			@RequestParam(name = "c", required = false)
			Long companyId,
			@Parameter(description = "Delivery status", required = false)
			@RequestParam(name = "s", required = false)
			Status status,
			Pageable pageable)
	{
		return deliveryService.getDeliveries(companyId, status, pageable);
	}


	@Operation(summary = "Add new delivery",
			responses = {
			@ApiResponse(responseCode = "200", description = "New delivery saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Delivery.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/deliveries")
	public Delivery addDelivery(@RequestBody Delivery delivery)
	{
		try
		{
			return deliveryService.addDelivery(delivery);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update delivery",
			responses = {
			@ApiResponse(responseCode = "200", description = "Updated delivery saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Delivery.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/deliveries/{id}")
	public Delivery updateDelivery(@PathVariable("id") Long deliveryId, @RequestBody Delivery delivery)
	{
		try
		{
			return deliveryService.updateDelivery(deliveryId, delivery);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete delivery",
			responses = {
			@ApiResponse(responseCode = "200", description = "Delivery deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/deliveries/{id}")
	public void deleteDelivery(@PathVariable("id") Long deliveryId)
	{
		try
		{
			deliveryService.deleteDelivery(deliveryId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Mark a delivery as delivered",
			responses = {
			@ApiResponse(responseCode = "200", description = "Updated delivery saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Delivery.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/deliveries/{id}/delivered")
	public Delivery setDelivered(Long deliveryId)
	{
		try
		{
			return deliveryService.setDelivered(deliveryId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete all deliveries (maintenance operation)",
			responses = {
			@ApiResponse(responseCode = "200", description = "All deliveries deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN"})
	@DeleteMapping("/api/deliveries/all")
	public void deleteAllDeliveries()
	{
		try
		{
			deliveryService.deleteAllDeliveries();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
