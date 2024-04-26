package com.ambraspace.etprodaja.model.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ambraspace.etprodaja.model.order.Order.Status;

public interface OrderRepository extends CrudRepository<Order, Long>, PagingAndSortingRepository<Order, Long>
{


	Optional<Order> findByWarehouseIdAndStatus(Long warehouseId, Status status);


	@Override
	@EntityGraph("order-with-details")
	Optional<Order> findById(Long id);


	@Query("""
SELECT o.id FROM Order o JOIN o.items i
WHERE
	o.warehouse.id = :w AND
	o.status = :s
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	Page<Long> findByWarehouseIdAndStatusAndOnlyUndelivered(
			@Param("w") Long warehouseId,
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
WHERE
	o.warehouse.id = :w AND
	o.status = :s
			""")
	Page<Long> findByWarehouseIdAndStatus(
			@Param("w") Long warehouseId,
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
WHERE
	o.warehouse.id = :w
			""")
	Page<Long> findByWarehouseId(
			@Param("w") Long warehouseId,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o JOIN o.items i
WHERE
	o.warehouse.id = :w
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	Page<Long> findByWarehouseIdAndOnlyUndelivered(
			@Param("w") Long warehouseId,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
WHERE
	o.status = :s
			""")
	Page<Long> findByStatus(
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o JOIN o.items i
WHERE
	o.status = :s
GROUP BY
	o.id
HAVING
	SUM(CASE i.delivery.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	Page<Long> findByStatusAndOnlyUndelivered(
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
			""")
	Page<Long> findAllOrders(Pageable pageable);


	@Query("""
SELECT o.id FROM Order o JOIN o.items i LEFT JOIN i.delivery d
GROUP BY
	o.id
HAVING
	SUM(CASE d.status WHEN 'DELIVERED' THEN 0 ELSE 1 END) > 0
			""")
	Page<Long> findByOnlyUndelivered(Pageable pageable);


	@Query("SELECT o FROM Order o WHERE o.id in (:ids)")
	@EntityGraph("order-with-details")
	Iterable<Order> getOrderDetails(@Param("ids") List<Long> ids, Sort sort);

}
