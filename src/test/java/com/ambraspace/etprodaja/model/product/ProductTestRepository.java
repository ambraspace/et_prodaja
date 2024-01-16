package com.ambraspace.etprodaja.model.product;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import jakarta.persistence.Tuple;

public interface ProductTestRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long>
{

	@Query(value = """
SELECT p.id, SUM(p.price), SUM(p.price * p.price)
FROM Product p
GROUP BY p.category
ORDER BY p.id
	""")
	List<Tuple> getTestData();
}
