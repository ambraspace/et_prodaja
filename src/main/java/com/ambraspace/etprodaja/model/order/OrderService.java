package com.ambraspace.etprodaja.model.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.order.Order.Status;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
import com.ambraspace.etprodaja.model.warehouse.WarehouseService;

@Service @Transactional
public class OrderService
{

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private ItemService itemService;


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

}
