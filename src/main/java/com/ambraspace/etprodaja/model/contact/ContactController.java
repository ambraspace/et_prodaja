package com.ambraspace.etprodaja.model.contact;

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

@RestController
public class ContactController
{

	@Autowired
	private ContactService contactService;


	@Operation(summary = "Get contacts by company", responses = {
			@ApiResponse(responseCode = "200", description = "List of contacts returned", content = {
					@Content(mediaType = "application/json", array =
							@ArraySchema(schema =
							@Schema(implementation = Contact.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/companies/{companyId}/contacts")
	public List<Contact> getContacts(@PathVariable Long companyId)
	{
		try
		{
			return contactService.getContacts(companyId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get a contact by company ID and contact ID", responses = {
			@ApiResponse(responseCode = "200", description = "Contact returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Contact.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@GetMapping("/api/companies/{companyId}/contacts/{id}")
	public Contact getContact(@PathVariable Long companyId, @PathVariable Long id)
	{
		try
		{
			return contactService.getContact(companyId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new contact",
			responses = {
			@ApiResponse(responseCode = "200", description = "Contact saved and returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Contact.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@PostMapping("/api/companies/{companyId}/contacts")
	public Contact addContact(@PathVariable Long companyId, @RequestBody Contact contact)
	{
		try
		{
			return contactService.addContact(companyId, contact);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update contact",
			responses = {
			@ApiResponse(responseCode = "200", description = "Contact updated (updated object returned)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Contact.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@PutMapping("/api/companies/{companyId}/contacts/{id}")
	public Contact updateContact(@PathVariable Long companyId, @PathVariable Long id, @RequestBody Contact contact)
	{
		try
		{
			return contactService.updateContact(companyId, id, contact);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete contact", responses = {
			@ApiResponse(responseCode = "200", description = "Contact deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@DeleteMapping("/api/companies/{companyId}/contacts/{id}")
	public void deleteContacts(@PathVariable Long companyId, @PathVariable Long id)
	{
		try
		{
			contactService.deleteContact(companyId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
