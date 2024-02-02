package com.ambraspace.etprodaja.model.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long>
{

	// Search by warehouse, tags and category
	@Query(value = """
SELECT DISTINCT p
FROM Product p CROSS JOIN StockInfo s JOIN p.tags tg
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	tg.id IN (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByWarehouseTagsAndCategory(
			@Param("w") Long warehouseId,
			@Param("t") List<Long> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse and tags
	@Query(value = """
SELECT DISTINCT p
FROM Product p CROSS JOIN StockInfo s JOIN p.tags tg
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	tg.id IN (:t)
			""")
	Page<Product> findByWarehouseAndTags(
			@Param("w") Long warehouseId,
			@Param("t") List<Long> tags, Pageable pageable);


	// Search by warehouse and category
	@Query(value = """
SELECT p
FROM Product p CROSS JOIN StockInfo s
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByWarehouseAndCategory(
			@Param("w") Long warehouseId,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse
	@Query(value = """
SELECT p
FROM Product p CROSS JOIN StockInfo s
WHERE
	p = s.product AND
	s.warehouse.id = :w
			""")
	Page<Product> findByWarehouse(
			@Param("w") Long warehouseId, Pageable pageable);


	// Search by warehouse, name, comment, tags and category
	@Query(value = """
SELECT DISTINCT p
FROM Product p CROSS JOIN StockInfo s JOIN p.tags tg
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%') AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByWarehouseNameCommentTagsAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<Long> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse, name, comment and tags
	@Query(value = """
SELECT DISTINCT p
FROM Product p CROSS JOIN StockInfo s JOIN p.tags tg
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%') AND
	tg.id in (:t)
			""")
	Page<Product> findByWarehouseNameCommentAndTags(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<Long> tags, Pageable pageable);


	// Search by warehouse, name, comment and category
	@Query(value = """
SELECT p
FROM Product p CROSS JOIN StockInfo s
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%') AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByWarehouseNameCommentAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse, name and comment
	@Query(value = """
SELECT p
FROM Product p CROSS JOIN StockInfo s
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%')
			""")
	Page<Product> findByWarehouseNameAndComment(
			@Param("w") Long warehouseId,
			@Param("q") String query, Pageable pageable);


	// Search by warehouse, name, tags and category
	@Query(value = """
SELECT DISTINCT p
FROM Product p CROSS JOIN StockInfo s JOIN p.tags tg
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	p.name LIKE '%:q%' AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByWarehouseNameTagsAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<Long> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse, name and tags
	@Query(value = """
SELECT DISTINCT p
FROM Product p CROSS JOIN StockInfo s JOIN p.tags tg
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	p.name LIKE '%:q%' AND
	tg.id in (:t)
			""")
	Page<Product> findByWarehouseNameAndTags(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<Long> tags, Pageable pageable);


	// Search by warehouse, name and category
	@Query(value = """
SELECT p
FROM Product p CROSS JOIN StockInfo s
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	p.name LIKE '%:q%' AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByWarehouseNameAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse and name
	@Query(value = """
SELECT p
FROM Product p CROSS JOIN StockInfo s
WHERE
	p = s.product AND
	s.warehouse.id = :w AND
	p.name LIKE '%:q%'
			""")
	Page<Product> findByWarehouseAndName(
			@Param("w") Long warehouseId,
			@Param("q") String query, Pageable pageable);


	// Search by tags and category
	@Query(value = """
SELECT DISTINCT p
FROM Product p JOIN p.tags tg
WHERE
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByTagsAndCategory(
			@Param("t") List<Long> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by tags
	@Query(value = """
SELECT DISTINCT p
FROM Product p JOIN p.tags tg
WHERE
	tg.id in (:t)
			""")
	Page<Product> findByTags(
			@Param("t") List<Long> tags, Pageable pageable);


	// Search by category
	Page<Product> findByCategory_IdIsIn(List<Long> categoryIds, Pageable pageable);


	// Search all
	// Defined by super-interface


	// Search by name, comment, tags and category
	@Query(value = """
SELECT DISTINCT p
FROM Product p JOIN p.tags tg
WHERE
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%') AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByNameCommentTagsAndCategory(
			@Param("q") String query,
			@Param("t") List<Long> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name, comment and tags
	@Query(value = """
SELECT DISTINCT p
FROM Product p JOIN p.tags tg
WHERE
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%') AND
	tg.id in (:t)
			""")
	Page<Product> findByNameCommentAndTags(
			@Param("q") String query,
			@Param("t") List<Long> tags, Pageable pageable);


	// Search by name, comment and category
	@Query(value = """
SELECT p
FROM Product p
WHERE
	(p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%') AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByNameCommentAndCategory(
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name and comment
	@Query(value = """
SELECT p
FROM Product p
WHERE
	p.name LIKE '%:q%' OR
	p.comment LIKE '%:q%'
			""")
	Page<Product> findByNameAndComment(
			@Param("q") String query, Pageable pageable);


	// Search by name, tags and category
	@Query(value = """
SELECT DISTINCT p
FROM Product p JOIN p.tags tg
WHERE
	p.name LIKE '%:q%' AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByNameTagsAndCategory(
			@Param("q") String query,
			@Param("t") List<Long> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name and tags
	@Query(value = """
SELECT DISTINCT p
FROM Product p JOIN p.tags tg
WHERE
	p.name LIKE '%:q%' AND
	tg.id in (:t)
			""")
	Page<Product> findByNameAndTags(
			@Param("q") String query,
			@Param("t") List<Long> tags, Pageable pageable);


	// Search by name and category
	@Query(value = """
SELECT p
FROM Product p
WHERE
	p.name LIKE '%:q%' AND
	p.category.id IN (:ct)
			""")
	Page<Product> findByNameAndCategory(
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name
	@Query(value = """
SELECT p
FROM Product p
WHERE
	p.name LIKE '%:q%'
			""")
	Page<Product> findByName(
			@Param("q") String query, Pageable pageable);

}
