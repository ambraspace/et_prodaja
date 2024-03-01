package com.ambraspace.etprodaja.model.order;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.order.Order.Status;

public interface OrderRepository extends CrudRepository<Order, Long>, PagingAndSortingRepository<Order, Long>
{

	Optional<Order> findByWarehouseIdAndStatus(Long warehouseId, Status status);

}
