package com.ambraspace.etprodaja.model.orderItem;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long>, PagingAndSortingRepository<OrderItem, Long>
{

	Iterable<OrderItem> findByOrderIdOrderById(String orderId);

	Iterable<OrderItem> findByIdIn(List<Long> ids);

}
