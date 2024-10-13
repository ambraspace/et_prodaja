package com.ambraspace.etprodaja.model.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
	private void deleteOrderById(String id)
	{

		Order fromRep = getOrder(id);

		if (fromRep == null)
		{
			throw new RuntimeException("No such order in the database!");
		}

		fromRep.getItems().forEach(i -> {
			i.setOrder(null);
		});

		itemService.updateItems(fromRep.getItems());

		orderRepository.deleteById(fromRep.getId());

	}


	@Transactional
	public void deleteAllOrders()
	{

		List<Order> orders = getOrders(null, null, false, Pageable.unpaged(Sort.by(Direction.DESC, "id"))).getContent();

		orders.forEach(o -> deleteOrderById(o.getId()));

	}


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
		{
			throw new RuntimeException("Error starting new order. No such warehouse!");
		}

		Order order = new Order();

		order.setStatus(Status.OPEN);
		order.setCreationDate(LocalDate.now());
		order.setWarehouse(wh);

		return orderRepository.save(order);

	}


	public Order getOrder(String id)
	{
		Order retVal = orderRepository.findById(id).orElse(null);
		if (retVal != null)
			fillTransientFields(List.of(retVal));
		return retVal;
	}


	public Page<Order> getOrders(Long warehouseId, Status status, boolean onlyUndelivered, Pageable pageable)
	{
		Page<String> orderIds = null;

		if (warehouseId == null)
		{
			if (status == null)
			{
				if (onlyUndelivered)
				{
					orderIds = getOrdersByOnlyUndelivered(pageable);
				} else {
					orderIds = getAllOrders(pageable);
				}
			} else {
				if (onlyUndelivered)
				{
					orderIds = getOrdersByStatusAndOnlyUndelivered(status, pageable);
				} else {
					orderIds = getOrdersByStatus(status, pageable);
				}
			}
		} else {
			if (status == null)
			{
				if (onlyUndelivered)
				{
					orderIds = getOrdersByWarehouseAndOnlyUndelivered(warehouseId, pageable);
				} else {
					orderIds = getOrdersByWarehouse(warehouseId, pageable);
				}
			} else {
				if (onlyUndelivered)
				{
					orderIds = getOrdersByWarehouseAndStatusAndOnlyUndelivered(warehouseId, status, pageable);
				} else {
					orderIds = getOrdersByWarehouseAndStatus(warehouseId, status, pageable);
				}
			}
		}

		List<Order> selectedOrders = new ArrayList<Order>();
		orderRepository.getOrderDetails(orderIds.getContent(), pageable.getSort()).forEach(selectedOrders::add);
		fillTransientFields(selectedOrders);
		return new PageImpl<Order>(selectedOrders, pageable, orderIds.getTotalElements());

	}


	private Page<String> getOrdersByWarehouseAndStatus(Long warehouseId, Status status, Pageable pageable)
	{
		return orderRepository.findByWarehouseIdAndStatus(warehouseId, status, pageable);
	}


	private Page<String> getOrdersByWarehouseAndStatusAndOnlyUndelivered(Long warehouseId, Status status,
			Pageable pageable)
	{
		return orderRepository.findByWarehouseIdAndStatusAndOnlyUndelivered(warehouseId, status, pageable);
	}


	private Page<String> getOrdersByWarehouse(Long warehouseId, Pageable pageable)
	{
		return orderRepository.findByWarehouseId(warehouseId, pageable);
	}


	private Page<String> getOrdersByWarehouseAndOnlyUndelivered(Long warehouseId, Pageable pageable)
	{
		return orderRepository.findByWarehouseIdAndOnlyUndelivered(warehouseId, pageable);
	}


	private Page<String> getOrdersByStatus(Status status, Pageable pageable)
	{
		return orderRepository.findByStatus(status, pageable);
	}


	private Page<String> getOrdersByStatusAndOnlyUndelivered(Status status, Pageable pageable)
	{
		return orderRepository.findByStatusAndOnlyUndelivered(status, pageable);
	}


	private Page<String> getAllOrders(Pageable pageable)
	{
		return orderRepository.findAllOrders(pageable);
	}


	private Page<String> getOrdersByOnlyUndelivered(Pageable pageable)
	{
		return orderRepository.findByOnlyUndelivered(pageable);
	}


	@Transactional
	public Order closeOrder(String orderId)
	{

		Order fromRep = getOrder(orderId);

		if (fromRep == null)
		{
			throw new RuntimeException("No such order in the database!");
		}

		if (fromRep.getStatus().equals(Status.CLOSED))
		{
			throw new RuntimeException("Selected order is already closed!");
		}

		fromRep.setClosureTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

		fromRep.setStatus(Status.CLOSED);

		return orderRepository.save(fromRep);

	}


	private void fillTransientFields(List<Order> orders)
	{

		for (Order o:orders)
		{

			BigDecimal value = BigDecimal.ZERO;

			if (o.getItems() != null && o.getItems().size() > 0)
			{
				for (Item i:o.getItems())
				{
					value = value.add(
							i.getStockInfo().getUnitPrice()
							.multiply(i.getQuantity())
							.setScale(2, RoundingMode.HALF_EVEN));
				}
			}

			o.setValue(value);

		}

	}


}
