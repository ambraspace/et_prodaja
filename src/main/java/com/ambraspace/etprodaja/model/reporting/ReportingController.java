package com.ambraspace.etprodaja.model.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReportingController
{

	@Autowired
	private ReportingService reportingService;


	@Operation(summary = "Download offer by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Offer downloaded", content = {
					@Content(mediaType = "application/pdf")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/files/offers/{fn}")
	public void downloadOffer(@PathVariable("fn") String fileName, HttpServletResponse response)
	{
		try
		{
			reportingService.downloadOffer(fileName, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}


	@Operation(summary = "Download order by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Order downloaded", content = {
					@Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/orders/{id}/dl")
	public void downloadOrder(@PathVariable long id, HttpServletResponse response)
	{
		try
		{
			reportingService.downloadOrder(id, response);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}




}
