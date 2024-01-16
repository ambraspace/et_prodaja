package com.ambraspace.etprodaja.model.offer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

public interface ItemRepository extends CrudRepository<Item, Long>, PagingAndSortingRepository<Item, Long>
{

	@Query(value = """
SELECT i.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i CROSS JOIN StockInfo s JOIN i.offer o
WHERE
	i.product = s.product AND
	i.warehouse = s.warehouse AND
	o.status = 'ACTIVE' AND
	o.validUntil >= CURRENT_DATE AND
	i.product in (:products)
GROUP BY i.product.id
ORDER BY i.product.id
	""")
	Iterable<Tuple> getItemDataForProductsInValidOffers(Iterable<Product> products);


	@Query(value = """
SELECT i.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i CROSS JOIN StockInfo s JOIN i.offer o
WHERE
	i.product = s.product AND
	i.warehouse = s.warehouse AND
	o.status <> 'ACTIVE' AND
	o.status <> 'CANCELED' AND
	o.validUntil >= CURRENT_DATE AND
	i.product in (:products)
GROUP BY i.product.id
ORDER BY i.product.id
	""")
	Iterable<Tuple> getItemDataForOrderedProducts(Iterable<Product> products);


	@Query(value = """
SELECT i.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i CROSS JOIN StockInfo s JOIN i.offer o
WHERE
	i.product = s.product AND
	i.warehouse = s.warehouse AND
	i.warehouse.id = :warehouseId AND
	o.status = 'ACTIVE' AND
	o.validUntil >= CURRENT_DATE AND
	i.product in (:products)
GROUP BY i.product.id
ORDER BY i.product.id
	""")
	Iterable<Tuple> getItemDataForProductsInValidOffersByWarehouse(Long warehouseId, Iterable<Product> products);


	@Query(value = """
SELECT i.product.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM Item i CROSS JOIN StockInfo s JOIN i.offer o
WHERE
	i.product = s.product AND
	i.warehouse = s.warehouse AND
	i.warehouse.id = :warehouseId AND
	o.status <> 'ACTIVE' AND
	o.status <> 'CANCELED' AND
	o.validUntil >= CURRENT_DATE AND
	i.product in (:products)
GROUP BY i.product.id
ORDER BY i.product.id
	""")
	Iterable<Tuple> getItemDataForOrderedProductsByWarehouse(Long warehouseId, Iterable<Product> products);

}
