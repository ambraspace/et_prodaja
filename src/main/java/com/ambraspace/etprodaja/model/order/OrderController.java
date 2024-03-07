package com.ambraspace.etprodaja.model.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.model.order.Order.Status;
import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;

@RestController
public class OrderController
{

	@Autowired
	private OrderService orderService;



	@Operation(summary = "Get order by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Order returned (may be null)", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Order.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/orders/{id}")
	public Order getOrder(@PathVariable("id") Long id)
	{
		return orderService.getOrder(id);
	}


	@Operation(summary = "Get all orders (paged)", responses = {
			@ApiResponse(responseCode = "200", description = "Page of orders returned", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = PageOrder.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@GetMapping("/api/orders")
	public Page<Order> getOrders(
			@Parameter(description = "Warehouse", required = false)
			@RequestParam(name = "w", required = false)
			Long warehouseId,
			@Parameter(description = "Order status", required = false)
			@RequestParam(name = "s", required = false)
			Status status,
			@Parameter(description = "Show only orders which have not been delivered yet", required = false)
			@RequestParam(name = "u", defaultValue = "false")
			boolean onlyUndelivered,
			@PageableDefault(sort = "id", direction = Direction.DESC)
			Pageable pageable)
	{
		return orderService.getOrders(warehouseId, status, onlyUndelivered, pageable);
	}


	@Operation(summary = "Close order",
			responses = {
			@ApiResponse(responseCode = "200", description = "Order closed", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = Order.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PutMapping("/api/orders/{id}/close")
	public Order closeOrder(@PathVariable("id") Long id)
	{
		try
		{
			return orderService.closeOrder(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


}
