package com.ambraspace.etprodaja.model.order;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.order.Order.Status;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
import com.ambraspace.etprodaja.model.warehouse.WarehouseService;

@Service
public class OrderService
{

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private ItemService itemService;


	@Transactional
	public void orderItems(List<Item> items)
	{

		items.forEach(i -> {
			orderItem(i);
		});

		itemService.updateItems(items);

	}


	private void orderItem(Item item)
	{
		Order parentOrder = getOpenOrderByWarehouseOrCreate(
				item.getStockInfo().getWarehouse().getCompany().getId(),
				item.getStockInfo().getWarehouse().getId());
		item.setOrder(parentOrder);
	}


	private Order getOpenOrderByWarehouseOrCreate(Long companyId, Long warehouseId)
	{
		Order openOrder = orderRepository.findByWarehouseIdAndStatus(warehouseId, Status.OPEN).orElse(null);
		if (openOrder == null)
		{
			openOrder = createEmptyOrder(companyId, warehouseId);
		}
		return openOrder;
	}


	private Order createEmptyOrder(Long companyId, Long warehouseId)
	{

		Warehouse wh = warehouseService.getWarehouse(companyId, warehouseId);

		if (wh == null)
			throw new RuntimeException("Error starting new order. No such warehouse!");

		Order order = new Order();

		order.setStatus(Status.OPEN);
		order.setWarehouse(wh);

		return orderRepository.save(order);

	}


	public Order getOrder(Long id)
	{
		return orderRepository.findById(id).orElse(null);
	}


	public Page<Order> getOrders(Long warehouseId, Status status, boolean onlyUndelivered, Pageable pageable)
	{
		if (warehouseId == null)
		{
			if (status == null)
			{
				if (onlyUndelivered)
				{
					return getOrdersByOnlyUndelivered(pageable);
				} else {
					return getAllOrders(pageable);
				}
			} else {
				if (onlyUndelivered)
				{
					return getOrdersByStatusAndOnlyUndelivered(status, pageable);
				} else {
					return getOrdersByStatus(status, pageable);
				}
			}
		} else {
			if (status == null)
			{
				if (onlyUndelivered)
				{
					return getOrdersByWarehouseAndOnlyUndelivered(warehouseId, pageable);
				} else {
					return getOrdersByWarehouse(warehouseId, pageable);
				}
			} else {
				if (onlyUndelivered)
				{
					return getOrdersByWarehouseAndStatusAndOnlyUndelivered(warehouseId, status, pageable);
				} else {
					return getOrdersByWarehouseAndStatus(warehouseId, status, pageable);
				}
			}
		}
	}


	private Page<Order> getOrdersByWarehouseAndStatus(Long warehouseId, Status status, Pageable pageable)
	{
		return orderRepository.findByWarehouseIdAndStatus(warehouseId, status, pageable);
	}


	private Page<Order> getOrdersByWarehouseAndStatusAndOnlyUndelivered(Long warehouseId, Status status,
			Pageable pageable)
	{
		return orderRepository.findByWarehouseIdAndStatusAndOnlyUndelivered(warehouseId, status, pageable);
	}


	private Page<Order> getOrdersByWarehouse(Long warehouseId, Pageable pageable)
	{
		return orderRepository.findByWarehouseId(warehouseId, pageable);
	}


	private Page<Order> getOrdersByWarehouseAndOnlyUndelivered(Long warehouseId, Pageable pageable)
	{
		return orderRepository.findByWarehouseIdAndOnlyUndelivered(warehouseId, pageable);
	}


	private Page<Order> getOrdersByStatus(Status status, Pageable pageable)
	{
		return orderRepository.findByStatus(status, pageable);
	}


	private Page<Order> getOrdersByStatusAndOnlyUndelivered(Status status, Pageable pageable)
	{
		return orderRepository.findByStatusAndOnlyUndelivered(status, pageable);
	}


	private Page<Order> getAllOrders(Pageable pageable)
	{
		return orderRepository.findAllOrders(pageable);
	}


	private Page<Order> getOrdersByOnlyUndelivered(Pageable pageable)
	{
		return orderRepository.findByOnlyUndelivered(pageable);
	}


	@Transactional
	public Order closeOrder(Long orderId)
	{

		Order fromRep = getOrder(orderId);

		if (fromRep == null)
			throw new RuntimeException("No such order in the database!");

		if (fromRep.getStatus().equals(Status.CLOSED))
			throw new RuntimeException("Selected order is already closed!");

		fromRep.setClosureTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

		fromRep.setStatus(Status.CLOSED);

		return orderRepository.save(fromRep);

	}


}
