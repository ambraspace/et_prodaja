package com.ambraspace.etprodaja.model.orderItem;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService
{

	@Autowired
	private OrderItemRepository orderItemRepository;


	public List<OrderItem> getItemsByOrderId(String orderId)
	{

		List<OrderItem> retVal = new ArrayList<OrderItem>();
		orderItemRepository.findByOrderIdOrderById(orderId).forEach(retVal::add);
		return retVal;

	}


	public void deleteById(Long id)
	{

		OrderItem item = orderItemRepository.findById(id).orElse(null);
		if (item == null)
			throw new RuntimeException("Order item not found!");

		orderItemRepository.deleteById(id);

	}


	public List<OrderItem> saveAllItems(List<OrderItem> items)
	{
		List<OrderItem> retVal = new ArrayList<OrderItem>();
		orderItemRepository.saveAll(items).forEach(retVal::add);
		return retVal;
	}

}
