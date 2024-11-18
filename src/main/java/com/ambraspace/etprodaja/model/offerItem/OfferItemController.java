package com.ambraspace.etprodaja.model.offerItem;

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
import com.ambraspace.etprodaja.util.Views;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class OfferItemController
{

	@Autowired
	private OfferItemService offerItemService;


	@Operation(summary = "Get an item by offer ID and item ID",
			responses = {
			@ApiResponse(responseCode = "200", description = "Item returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = OfferItem.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/offers/{offerId}/items/{id}")
	@JsonView(Views.OfferItem.class)
	public OfferItem getOfferItem(@PathVariable("offerId") String offerId, @PathVariable("id") Long id)
	{
		return offerItemService.getOfferItem(offerId, id);
	}


	@Operation(summary = "Get items by offer", responses = {
			@ApiResponse(responseCode = "200", description = "List of items returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = OfferItem.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/offers/{offerId}/items")
	@JsonView(Views.OfferItem.class)
	public List<OfferItem> getOfferItems(@PathVariable("offerId") String offerId)
	{
		return offerItemService.getOfferItems(offerId);
	}


	@Operation(summary = "Adds items to the offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Items saved and returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = OfferItem.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/offers/{offerId}/items")
	@JsonView(Views.OfferItem.class)
	public List<OfferItem> addItems(@PathVariable("offerId") String offerId, @RequestBody List<OfferItem> items)
	{
		try
		{
			return offerItemService.addItems(offerId, items);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	@Operation(summary = "Updates existing items",
			responses = {
			@ApiResponse(responseCode = "200", description = "Items saved and returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = OfferItem.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/offers/{offerId}/items")
	@JsonView(Views.OfferItem.class)
	public List<OfferItem> updateItems(
			@PathVariable("offerId") String offerId,
			@RequestBody List<OfferItem> items)
	{
		try
		{
			return offerItemService.updateItems(offerId, items);
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
			offerItemService.deleteItem(offerId, itemId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
