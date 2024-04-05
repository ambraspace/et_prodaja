package com.ambraspace.etprodaja.model.offer;

import java.security.Principal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.model.offer.Offer.Status;
import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class OfferController
{

	@Autowired
	private OfferService offerService;

	@Autowired
	private WorkflowService workflowService;


	@Operation(summary = "Get offer by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Offer returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Offer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/offers/{id}")
	public Offer getOffer(@PathVariable("id") String offerId)
	{
		return offerService.getOffer(offerId);
	}


	@Operation(summary = "Get all offers (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of offers returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageOffer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/offers")
	public Page<Offer> getOffers(
			@Parameter(description = "Username", required = false)
			@RequestParam(name = "u", required = false) String username,
			@Parameter(description = "Company ID", required = false)
			@RequestParam(name = "c", required = false) Long companyId,
			@Parameter(description = "Offer status", required = false)
			@RequestParam(name = "s", defaultValue = "ACTIVE") Status status,
			@Parameter(description = "Show only overdue offers", required = false)
			@RequestParam(name = "o", defaultValue = "false") boolean onlyOverdue,
			@ParameterObject @PageableDefault(sort = {"offerDate", "id"}, direction = Direction.DESC) Pageable pageable)
	{
		return offerService.getOffers(username, companyId, status, onlyOverdue, pageable);
	}


	@Operation(summary = "Add new offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "New offer saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Offer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/offers")
	public Offer addOffer(@RequestBody Offer offer, Principal principal)
	{
		try
		{
			return offerService.addOffer(offer, principal.getName());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update existing offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Offer updated and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Offer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/offers/{id}")
	public Offer updateOffer(@PathVariable("id") String offerId, @RequestBody Offer offer, Principal principal)
	{
		try
		{
			return offerService.updateOffer(offerId, offer, principal.getName());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
		}
	}


	@Operation(summary = "Delete an offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Offer deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/offers/{id}")
	public void deleteOffer(@PathVariable("id") String offerId)
	{
		try
		{
			offerService.deleteOffer(offerId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Cancels an offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Offer canceled and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Offer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PatchMapping("/api/offers/{id}/cancel")
	public Offer cancelOffer(
			@PathVariable("id") String offerId,
			@Parameter(description = "Reason for cancelation")
			@RequestParam(name = "r", required = false) String reason)
	{
		try
		{
			return workflowService.cancelOffer(offerId, reason);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Accepts an offer and submits its items to order(s)",
			responses = {
			@ApiResponse(responseCode = "200", description = "Offer accepted and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Offer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PatchMapping("/api/offers/{id}/accept")
	public Offer acceptOffer(@PathVariable("id") String offerId)
	{
		try
		{
			return workflowService.acceptOffer(offerId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Duplicates an offer",
			responses = {
			@ApiResponse(responseCode = "200", description = "Offer duplicated and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Offer.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/offers/{id}/duplicate")
	public Offer duplicateOffer(@PathVariable("id") String offerId, Principal principal)
	{
		try
		{
			return workflowService.duplicateOffer(offerId, principal.getName());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete all offers (maintenance operation)",
			responses = {
			@ApiResponse(responseCode = "200", description = "All offers deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN"})
	@DeleteMapping("/api/offers/all")
	public void deleteAllOffers()
	{
		try
		{
			offerService.deleteAllOffers();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
