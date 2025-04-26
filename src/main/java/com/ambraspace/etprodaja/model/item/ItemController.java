package com.ambraspace.etprodaja.model.item;

import java.util.List;

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

import com.ambraspace.etprodaja.util.ErrorResponse;
import com.ambraspace.etprodaja.util.Views;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	@JsonView(Views.Item.class)
	public Item getOfferItem(@PathVariable String offerId, @PathVariable Long id)
	{
		return itemService.getOfferItem(offerId, id);
	}


	@Operation(summary = "Get an item by order ID and item ID",
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
	@GetMapping("/api/orders/{orderId}/items/{id}")
	@JsonView(Views.Item.class)
	public Item getOrderItem(@PathVariable String orderId, @PathVariable Long id)
	{
		return itemService.getOrderItem(orderId, id);
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
	@JsonView(Views.Item.class)
	public List<Item> getOfferItems(@PathVariable String offerId)
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
	@JsonView(Views.Item.class)
	public List<Item> getOrderItems(
			@Parameter(description = "Order ID", required = false)
			@PathVariable("orderId") String offerId,
			@Parameter(description = "Whether to return only items which have not been ordered", required = false)
			@RequestParam(name = "ou", defaultValue = "false") boolean onlyUndelivered
			)
	{
		return itemService.getOrderItems(offerId, onlyUndelivered);
	}


	@Operation(summary = "Get items which haven't been ordered yet", responses = {
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
	@GetMapping("/api/items")
	@JsonView(Views.Item.class)
	public Page<Item> getUnorderedItems(
		@RequestParam("s") Long supplierId,
		Pageable pageable
	)
	{
		return itemService.getUnorderedItems(supplierId, pageable);
	}


	@Operation(summary = "Adds items to the offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Items saved and returned", content = {
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
	@PostMapping("/api/offers/{offerId}/items")
	@JsonView(Views.Item.class)
	public List<Item> addItems(@PathVariable String offerId, @RequestBody List<Item> items)
	{
		try
		{
			return itemService.addItems(offerId, items);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	@Operation(summary = "Updates existing items",
			responses = {
			@ApiResponse(responseCode = "200", description = "Items saved and returned", content = {
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
	@PutMapping("/api/offers/{offerId}/items")
	@JsonView(Views.Item.class)
	public List<Item> updateItems(
			@PathVariable String offerId,
			@RequestBody List<Item> items)
	{
		try
		{
			return itemService.updateItems(offerId, items);
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
			@PathVariable String offerId,
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
