package com.ambraspace.etprodaja.model.company;

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
public class CompanyController
{

	@Autowired
	private CompanyService companyService;


	@Operation(summary = "Get all companies (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of companies returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageCompany.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/companies")
	public Page<Company> getCompanies(
			@Parameter(description = "Query by which to search for companies")
			@RequestParam(name = "q", required = false)
			String query,
			@ParameterObject @PageableDefault(sort = "name") Pageable pageable)
	{
		try
		{
			return companyService.getCompanies(query, pageable);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get a company by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Company returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Company.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/companies/{id}")
	public Company getCompany(@PathVariable Long id)
	{
		return companyService.getCompany(id);
	}


	@Operation(summary = "Add new company", responses = {
			@ApiResponse(responseCode = "200", description = "Company added", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Company.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/companies")
	public Company addCompany(@RequestBody Company company)
	{
		try
		{
			return companyService.addCompany(company);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update company", responses = {
			@ApiResponse(responseCode = "200", description = "Company updated (updated object returned)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Company.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/companies/{id}")
	public Company updateCompany(@PathVariable Long id, @RequestBody Company company)
	{
		try
		{
			return companyService.updateCompany(id, company);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete company by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Company deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/companies/{id}")
	public void deleteCompany(@PathVariable Long id)
	{
		try
		{
			companyService.deleteCompany(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
