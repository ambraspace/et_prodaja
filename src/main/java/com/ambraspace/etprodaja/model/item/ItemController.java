package com.ambraspace.etprodaja.model.item;

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
public class ItemController
{

	@Autowired
	private ItemService itemService;


	@Operation(summary = "Get an item by offer ID and item ID",
			responses = {
			@ApiResponse(responseCode = "200", description = "Item returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Item.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/offers/{offerId}/items/{id}")
	public Item getItem(@PathVariable("offerId") String offerId, @PathVariable("id") Long id)
	{
		return itemService.getOfferItem(offerId, id);
	}


	@Operation(summary = "Get items by offer", responses = {
			@ApiResponse(responseCode = "200", description = "List of items returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = Item.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/offers/{offerId}/items")
	public List<Item> getOfferItems(@PathVariable("offerId") String offerId)
	{
		return itemService.getOfferItems(offerId);
	}


	@Operation(summary = "Get items by order", responses = {
			@ApiResponse(responseCode = "200", description = "List of items returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = Item.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/orders/{orderId}/items")
	public List<Item> getOrderItems(@PathVariable("orderId") Long offerId)
	{
		return itemService.getOrderItems(offerId);
	}


	@Operation(summary = "Get items by delivery", responses = {
			@ApiResponse(responseCode = "200", description = "List of items returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = Item.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/deliveries/{deliveryId}/items")
	public List<Item> getDeliveryItems(@PathVariable("deliveryId") Long deliveryId)
	{
		return itemService.getDeliveryItems(deliveryId);
	}


	@Operation(summary = "Adds an item to the offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Item saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Item.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/offers/{offerId}/items")
	public Item addItem(@PathVariable("offerId") String offerId, @RequestBody Item item)
	{
		try
		{
			return itemService.addItem(offerId, item);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	@Operation(summary = "Updates existing item",
			responses = {
			@ApiResponse(responseCode = "200", description = "Item saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Item.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/offers/{offerId}/items/{id}")
	public Item updateItem(
			@PathVariable("offerId") String offerId,
			@PathVariable("id") Long itemId,
			@RequestBody Item item)
	{
		try
		{
			return itemService.updateItem(offerId, itemId, item);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	@Operation(summary = "Deletes existing item",
			responses = {
			@ApiResponse(responseCode = "200", description = "Item deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/offers/{offerId}/items/{id}")
	public void deleteItem(
			@PathVariable("offerId") String offerId,
			@PathVariable("id") Long itemId)
	{
		try
		{
			itemService.deleteItem(offerId, itemId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
