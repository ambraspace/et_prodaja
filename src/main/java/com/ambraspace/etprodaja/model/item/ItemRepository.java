package com.ambraspace.etprodaja.model.item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

public interface ItemRepository extends CrudRepository<Item, Long>, PagingAndSortingRepository<Item, Long>
{

	@EntityGraph("offer-items")
	Optional<Item> findByOfferIdAndId(String offerId, Long id);


	@EntityGraph("order-items")
	Optional<Item> findByOrderIdAndId(String orderId, Long id);


	@EntityGraph("items")
	Iterable<Item> findByOfferId(String offerId);


	@EntityGraph("items")
	Iterable<Item> findByOrderId(String orderId);

	@Query("""
SELECT DISTINCT i FROM Item i LEFT JOIN i.deliveryItems di
WHERE
	i.order.id = :orderId
GROUP BY
	i.id
HAVING i.quantity > SUM(COALESCE(di.quantity, 0))
ORDER BY i.id
			""")
	@EntityGraph("items")
	Iterable<Item> findByOrderIdOnlyUndelivered(@Param("orderId") String orderId);


	@Query(value = """
			SELECT i FROM Item i LEFT JOIN i.deliveryItems di
			WHERE i.order IS NOT NULL AND i.order.status = 'CLOSED'
			GROUP BY i.id, i.quantity
			HAVING i.quantity > SUM(COALESCE(di.quantity, 0))
			""")
	@EntityGraph("delivery-items")
	Page<Item> findOnlyUndelivered(Pageable pageable);


	@Query("""
SELECT
	i.id, SUM(COALESCE(di.quantity, 0))
FROM
	Item i LEFT JOIN i.deliveryItems di
WHERE
	i.id in (:ids)
GROUP BY
	i.id
			""")
	List<Tuple> getOrderedQtys(Iterable<Long> ids);


	void deleteByOfferIdAndId(String offerId, Long id);


	@Query(value = """
SELECT s.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i JOIN i.stockInfo s JOIN i.offer o
WHERE
	o.status = 'ACTIVE' AND
	o.validUntil >= CURRENT_DATE AND
	s.product in (:products)
GROUP BY s.product.id
ORDER BY s.product.id
	""")
	Iterable<Tuple> getItemDataForProductsInValidOffers(Iterable<Product> products);


	@Query(value = """
SELECT s.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i JOIN i.stockInfo s JOIN i.offer o
WHERE
	o.status <> 'ACTIVE' AND
	o.status <> 'CANCELED' AND
	s.product in (:products)
GROUP BY s.product.id
ORDER BY s.product.id
	""")
	Iterable<Tuple> getItemDataForOrderedProducts(Iterable<Product> products);


	@Query(value = """
SELECT s.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i JOIN i.stockInfo s JOIN i.offer o
WHERE
	s.warehouse.id = :warehouseId AND
	o.status = 'ACTIVE' AND
	o.validUntil >= CURRENT_DATE AND
	s.product in (:products)
GROUP BY s.product.id
ORDER BY s.product.id
	""")
	Iterable<Tuple> getItemDataForProductsInValidOffersByWarehouse(Long warehouseId, Iterable<Product> products);


	@Query(value = """
SELECT s.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i JOIN i.stockInfo s JOIN i.offer o
WHERE
	s.warehouse.id = :warehouseId AND
	o.status <> 'ACTIVE' AND
	o.status <> 'CANCELED' AND
	s.product in (:products)
GROUP BY s.product.id
ORDER BY s.product.id
	""")
	Iterable<Tuple> getItemDataForOrderedProductsByWarehouse(Long warehouseId, Iterable<Product> products);


	@Query("""
SELECT SUM(i.quantity)
FROM Item i
WHERE
	i.stockInfo.id = :id AND
	i.offer.status = 'ACTIVE' AND
	i.offer.validUntil >= CURRENT_DATE
			""")
	BigDecimal getOfferedStockInfoQty(Long id);


	@Query("""
SELECT SUM(i.quantity)
FROM Item i
WHERE
	i.stockInfo.id = :id AND
	i.offer.status <> 'ACTIVE' AND
	i.offer.status <> 'CANCELED'
			""")
	BigDecimal getOrderedStockInfoQty(Long id);


}
