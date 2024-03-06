package com.ambraspace.etprodaja.model.stockinfo;

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

public interface StockInfoRepository extends CrudRepository<StockInfo, Long>, PagingAndSortingRepository<StockInfo, Long>
{

	@Query(value = """
SELECT
	s.product.id,
	SUM(s.quantity),
	SUM(s.quantity * s.unitPrice)
FROM StockInfo s
WHERE s.product IN (:products)
GROUP BY s.product.id
ORDER BY s.product.id
			""")
	Iterable<Tuple> getStockInfoByProducts(@Param("products") Iterable<Product> products);


	Iterable<StockInfo> findByWarehouseIdAndProductInOrderByProduct(Long warehouseId, Iterable<Product> products);


	@EntityGraph("stockinfo-with-warehouse-and-company")
	Optional<StockInfo> findByProductIdAndId(Long productId, Long id);


	@EntityGraph("stockinfo-with-warehouse-and-company")
	Page<StockInfo> findByProductId(Long productId, Pageable pageable);

}
