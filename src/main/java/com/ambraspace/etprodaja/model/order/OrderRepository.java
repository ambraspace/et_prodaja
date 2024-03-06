package com.ambraspace.etprodaja.model.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ambraspace.etprodaja.model.order.Order.Status;

public interface OrderRepository extends CrudRepository<Order, Long>, PagingAndSortingRepository<Order, Long>
{


	Optional<Order> findByWarehouseIdAndStatus(Long warehouseId, Status status);


	@Query("""
SELECT o FROM Order o JOIN o.items i
WHERE
	o.warehouse.id = :w AND
	o.status = :s
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByWarehouseIdAndStatusAndOnlyUndelivered(
			@Param("w") Long warehouseId,
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o FROM Order o
WHERE
	o.warehouse.id = :w AND
	o.status = :s
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByWarehouseIdAndStatus(
			@Param("w") Long warehouseId,
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o FROM Order o
WHERE
	o.warehouse.id = :w
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByWarehouseId(
			@Param("w") Long warehouseId,
			Pageable pageable);


	@Query("""
SELECT o FROM Order o JOIN o.items i
WHERE
	o.warehouse.id = :w
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByWarehouseIdAndOnlyUndelivered(
			@Param("w") Long warehouseId,
			Pageable pageable);


	@Query("""
SELECT o FROM Order o
WHERE
	o.status = :s
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByStatus(
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o FROM Order o JOIN o.items i
WHERE
	o.status = :s
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByStatusAndOnlyUndelivered(
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o FROM Order o
			""")
	@EntityGraph("order-with-details")
	Page<Order> findAllOrders(Pageable pageable);


	@Query("""
SELECT o FROM Order o JOIN o.items i
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	@EntityGraph("order-with-details")
	Page<Order> findByOnlyUndelivered(Pageable pageable);


}
