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

public interface OrderRepository extends CrudRepository<Order, String>, PagingAndSortingRepository<Order, String>
{


	Optional<Order> findByWarehouseIdAndStatus(Long warehouseId, Status status);


	@Override
	@EntityGraph("order-with-details")
	Optional<Order> findById(String id);


	// TODO: Može li se ovaj Query optimizovati?
	@Query("""
SELECT DISTINCT o1.id FROM Order o1 WHERE EXISTS (
	SELECT o2.id, i.quantity, SUM(COALESCE(di.quantity, 0)) FROM Order o2 JOIN o2.items i LEFT JOIN i.deliveryItems di
	WHERE
		o1.id = o2.id AND
		o2.warehouse.id = :w AND
		o2.status = :s
	GROUP BY
		i.id
	HAVING
		i.quantity > SUM(COALESCE(di.quantity, 0))
)
			""")
	Page<String> findByWarehouseIdAndStatusAndOnlyUndelivered(
			@Param("w") Long warehouseId,
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
WHERE
	o.warehouse.id = :w AND
	o.status = :s
			""")
	Page<String> findByWarehouseIdAndStatus(
			@Param("w") Long warehouseId,
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
WHERE
	o.warehouse.id = :w
			""")
	Page<String> findByWarehouseId(
			@Param("w") Long warehouseId,
			Pageable pageable);


	@Query("""
SELECT DISTINCT o1.id FROM Order o1 WHERE EXISTS (
	SELECT o2.id, i.quantity, SUM(COALESCE(di.quantity, 0)) FROM Order o2 JOIN o2.items i LEFT JOIN i.deliveryItems di
	WHERE
		o1.id = o2.id AND
		o2.warehouse.id = :w
	GROUP BY
		i.id
	HAVING
		i.quantity > SUM(COALESCE(di.quantity, 0))
)
			""")
	Page<String> findByWarehouseIdAndOnlyUndelivered(
			@Param("w") Long warehouseId,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
WHERE
	o.status = :s
			""")
	Page<String> findByStatus(
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT DISTINCT o1.id FROM Order o1 WHERE EXISTS (
	SELECT o2.id, i.quantity, SUM(COALESCE(di.quantity, 0)) FROM Order o2 JOIN o2.items i LEFT JOIN i.deliveryItems di
	WHERE
		o1.id = o2.id AND
		o2.status = :s
	GROUP BY
		i.id
	HAVING
		i.quantity > SUM(COALESCE(di.quantity, 0))
)
			""")
	Page<String> findByStatusAndOnlyUndelivered(
			@Param("s") Status status,
			Pageable pageable);


	@Query("""
SELECT o.id FROM Order o
			""")
	Page<String> findAllOrders(Pageable pageable);


	@Query("""
SELECT DISTINCT o1.id FROM Order o1 WHERE EXISTS (
	SELECT o2.id, i.quantity, SUM(COALESCE(di.quantity, 0)) FROM Order o2 JOIN o2.items i LEFT JOIN i.deliveryItems di
	WHERE
		o1.id = o2.id
	GROUP BY
		i.id
	HAVING
		i.quantity > SUM(COALESCE(di.quantity, 0))
)
			""")
	Page<String> findByOnlyUndelivered(Pageable pageable);


	@Query("SELECT o FROM Order o WHERE o.id in (:ids)")
	@EntityGraph("order-with-details")
	Iterable<Order> getOrderDetails(@Param("ids") List<String> ids, Sort sort);

}
