package com.ambraspace.etprodaja.model.item;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

public interface ItemRepository extends CrudRepository<Item, Long>, PagingAndSortingRepository<Item, Long>
{

	@EntityGraph("offer-items")
	Optional<Item> findByOfferIdAndId(String offerId, Long id);


	@EntityGraph("offer-items")
	Iterable<Item> findByOfferId(String offerId);


	@EntityGraph("order-items")
	Iterable<Item> findByOrderId(Long orderId);


	@EntityGraph("delivery-items")
	Iterable<Item> findByDeliveryId(Long deliveryId);


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

}
