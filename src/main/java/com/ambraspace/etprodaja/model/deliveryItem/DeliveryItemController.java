package com.ambraspace.etprodaja.model.deliveryItem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class DeliveryItemController
{

	@Autowired
	private DeliveryItemService deliveryItemService;


	@Operation(summary = "Get delivery item", responses = {
			@ApiResponse(responseCode = "200", description = "Delivery item returned (may be null)", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryItem.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/deliveries/{deliveryId}/deliveryItems/{id}")
	public DeliveryItem getDeliveryItem(@PathVariable String deliveryId, @PathVariable Long id)
	{
		try
		{
			return deliveryItemService.getDeliveryItem(deliveryId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get delivery items by delivery ID", responses = {
			@ApiResponse(responseCode = "200", description = "List of delivery items returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = DeliveryItem.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/deliveries/{deliveryId}/deliveryItems")
	public List<DeliveryItem> getDeliveryItems(@PathVariable String deliveryId)
	{
		try
		{
			return deliveryItemService.getDeliveryItems(deliveryId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new delivery items", responses = {
			@ApiResponse(responseCode = "200", description = "A delivery items saved and returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = DeliveryItem.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/deliveries/{deliveryId}/deliveryItems")
	public List<DeliveryItem> addDeliveryItems(
			@PathVariable String deliveryId,
			@RequestBody List<DeliveryItem> dis)
	{
		try
		{
			return deliveryItemService.addDeliveryItems(deliveryId, dis);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}



	@Operation(summary = "Update existing delivery item", responses = {
			@ApiResponse(responseCode = "200", description = "A delivery item updated and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = DeliveryItem.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/deliveries/{deliveryId}/deliveryItems/{id}")
	public DeliveryItem updateDeliveryItem(
			@PathVariable String deliveryId,
			@PathVariable Long id,
			@RequestBody DeliveryItem di)
	{
		try
		{
			return deliveryItemService.updateDeliveryItem(deliveryId, id, di);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete existing delivery item", responses = {
			@ApiResponse(responseCode = "200", description = "A delivery item deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/deliveries/{deliveryId}/deliveryItems/{id}")
	public void deleteDeliveryItem(
			@PathVariable String deliveryId,
			@PathVariable Long id)
	{
		try
		{
			deliveryItemService.deleteDeliveryItem(deliveryId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
