package com.ambraspace.etprodaja.model.stockinfo;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class StockInfoController
{

	@Autowired
	private StockInfoService stockInfoService;


	@Operation(summary = "Get StockInfo by ID",
			responses = {
			@ApiResponse(responseCode = "200", description = "Stock info returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = StockInfo.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/products/{productId}/stockInfos/{id}")
	public StockInfo getStockInfo(@PathVariable Long productId, @PathVariable Long id)
	{
		try
		{
			return stockInfoService.getStockInfo(productId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Get StockInfo by product",
			responses = {
			@ApiResponse(responseCode = "200", description = "Page of StockInfo objects returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageStockInfo.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/products/{productId}/stockInfos")
	public Page<StockInfo> getStockInfosByProduct(
			@PathVariable Long productId,
			@ParameterObject @PageableDefault(sort = "warehouse.name") Pageable pageable)
	{
		try
		{
			return stockInfoService.getStockInfosByProduct(productId, pageable);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Add new stock info",
				responses = {
				@ApiResponse(responseCode = "200", description = "Stock info saved and returned", content = {
						@Content(mediaType = "application/json", schema =
								@Schema(implementation = StockInfo.class))
				}),
				@ApiResponse(responseCode = "400", description = "Bad request", content = {
						@Content(mediaType = "application/json", schema =
								@Schema(implementation = ErrorResponse.class))
				})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping("/api/products/{productId}/stockInfos")
	public StockInfo addStockInfo(@PathVariable Long productId, @RequestBody StockInfo si)
	{
		try
		{
			return stockInfoService.addStockInfo(productId, si);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Update stock info",
				responses = {
				@ApiResponse(responseCode = "200", description = "Stock info updated and updated object returned", content = {
						@Content(mediaType = "application/json", schema =
								@Schema(implementation = StockInfo.class))
				}),
				@ApiResponse(responseCode = "400", description = "Bad request", content = {
						@Content(mediaType = "application/json", schema =
								@Schema(implementation = ErrorResponse.class))
				})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/products/{productId}/stockInfos/{id}")
	public StockInfo updateStockInfo(
			@PathVariable Long productId,
			@PathVariable Long id,
			@RequestBody StockInfo si)
	{
		try
		{
			return stockInfoService.updateStockInfo(productId, id, si);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@Operation(summary = "Delete stock info",
			responses = {
			@ApiResponse(responseCode = "200", description = "Stock info deleted", content = {
					@Content(mediaType = "application/json")
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@DeleteMapping("/api/products/{productId}/stockInfos/{id}")
	public void deleteStockInfo(@PathVariable Long productId, @PathVariable Long id)
	{
		try
		{
			stockInfoService.deleteStockInfo(productId, id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
