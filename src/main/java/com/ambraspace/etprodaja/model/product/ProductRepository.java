package com.ambraspace.etprodaja.model.product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ambraspace.etprodaja.model.category.Category;

import jakarta.persistence.Tuple;


public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long>
{

	// Search by warehouse, tags and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s JOIN p.tags tg
WHERE
	s.warehouse.id = :w AND
	tg.id IN (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByWarehouseTagsAndCategory(
			@Param("w") Long warehouseId,
			@Param("t") List<String> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse and tags
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s JOIN p.tags tg
WHERE
	s.warehouse.id = :w AND
	tg.id IN (:t)
			""")
	Page<Long> findByWarehouseAndTags(
			@Param("w") Long warehouseId,
			@Param("t") List<String> tags, Pageable pageable);


	// Search by warehouse and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s
WHERE
	s.warehouse.id = :w AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByWarehouseAndCategory(
			@Param("w") Long warehouseId,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s
WHERE
	s.warehouse.id = :w
			""")
	Page<Long> findByWarehouse(
			@Param("w") Long warehouseId, Pageable pageable);


	// Search by warehouse, name, comment, tags and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s JOIN p.tags tg
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q) AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByWarehouseNameCommentTagsAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<String> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse, name, comment and tags
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s JOIN p.tags tg
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q) AND
	tg.id in (:t)
			""")
	Page<Long> findByWarehouseNameCommentAndTags(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<String> tags, Pageable pageable);


	// Search by warehouse, name, comment and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByWarehouseNameCommentAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse, name and comment
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q)
			""")
	Page<Long> findByWarehouseNameAndComment(
			@Param("w") Long warehouseId,
			@Param("q") String query, Pageable pageable);


	// Search by warehouse, name, tags and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s JOIN p.tags tg
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q) AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByWarehouseNameTagsAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<String> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse, name and tags
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s JOIN p.tags tg
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q) AND
	tg.id in (:t)
			""")
	Page<Long> findByWarehouseNameAndTags(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("t") List<String> tags, Pageable pageable);


	// Search by warehouse, name and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByWarehouseNameAndCategory(
			@Param("w") Long warehouseId,
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by warehouse and name
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.stockInfos s
WHERE
	s.warehouse.id = :w AND
	(p.reference LIKE :q OR
	p.description LIKE :q)
			""")
	Page<Long> findByWarehouseAndName(
			@Param("w") Long warehouseId,
			@Param("q") String query, Pageable pageable);


	// Search by tags and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.tags tg
WHERE
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByTagsAndCategory(
			@Param("t") List<String> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by tags
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.tags tg
WHERE
	tg.id in (:t)
			""")
	Page<Long> findByTags(
			@Param("t") List<String> tags, Pageable pageable);


	// Search by category
	@Query(value = """
SELECT p.id
FROM Product p
WHERE
	p.category.id IN (:ct)
			""")
	Page<Long> findByCategory(@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search all
	@Query(value = """
SELECT p.id
FROM Product p
			""")
	Page<Long> findAllProducts(Pageable pageable);


	// Search by name, comment, tags and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.tags tg
WHERE
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q) AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByNameCommentTagsAndCategory(
			@Param("q") String query,
			@Param("t") List<String> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name, comment and tags
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.tags tg
WHERE
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q) AND
	tg.id in (:t)
			""")
	Page<Long> findByNameCommentAndTags(
			@Param("q") String query,
			@Param("t") List<String> tags, Pageable pageable);


	// Search by name, comment and category
	@Query(value = """
SELECT p.id
FROM Product p
WHERE
	(p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByNameCommentAndCategory(
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name and comment
	@Query(value = """
SELECT p.id
FROM Product p
WHERE
	p.reference LIKE :q OR
	p.description LIKE :q OR
	p.comment LIKE :q
			""")
	Page<Long> findByNameAndComment(
			@Param("q") String query, Pageable pageable);


	// Search by name, tags and category
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.tags tg
WHERE
	(p.reference LIKE :q OR
	p.description LIKE :q) AND
	tg.id in (:t) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByNameTagsAndCategory(
			@Param("q") String query,
			@Param("t") List<String> tags,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name and tags
	@Query(value = """
SELECT DISTINCT p.id
FROM Product p JOIN p.tags tg
WHERE
	(p.reference LIKE :q OR
	p.description LIKE :q) AND
	tg.id in (:t)
			""")
	Page<Long> findByNameAndTags(
			@Param("q") String query,
			@Param("t") List<String> tags, Pageable pageable);


	// Search by name and category
	@Query(value = """
SELECT p.id
FROM Product p
WHERE
	(p.reference LIKE :q OR
	p.description LIKE :q) AND
	p.category.id IN (:ct)
			""")
	Page<Long> findByNameAndCategory(
			@Param("q") String query,
			@Param("ct") List<Long> categoryIds, Pageable pageable);


	// Search by name
	@Query(value = """
SELECT p.id
FROM Product p
WHERE
	p.reference LIKE :q OR
	p.description LIKE :q
			""")
	Page<Long> findByName(
			@Param("q") String query, Pageable pageable);


	@EntityGraph("product-with-previews")
	@Query("SELECT p FROM Product p where p.id IN (:ids)")
	Iterable<Product> getProductsById(Iterable<Long> ids, Sort sort);


	@EntityGraph(value = "product-with-previews")
	@Query("SELECT p FROM Product p where p.id = :id")
	Optional<Product> getProductWithPreviews(@Param("id") Long id);


	@EntityGraph(value = "product-with-category-and-tags")
	@Query("SELECT p FROM Product p where p.id = :id")
	Optional<Product> getProductWithCategoryAndTags(@Param("id") Long id);


	Iterable<Product> findByCategoryIn(Set<Category> categories);


	@Query("""
SELECT p.id, SUM(s.quantity), SUM(s.quantity * s.unitPrice), SUM(s.repairableQuantity)
FROM Product p JOIN p.stockInfos s
WHERE p in (:products)
GROUP BY p.id
ORDER BY p.id
			""")
	List<Tuple> getStockQtyAndValue(List<Product> products);


	@Query("""
SELECT p.id, SUM(s.quantity), SUM(s.quantity * s.unitPrice), SUM(s.repairableQuantity)
FROM Product p JOIN p.stockInfos s
WHERE
	p in (:products) AND
	s.warehouse.id = :warehouseId
GROUP BY p.id
ORDER BY p.id
			""")
	List<Tuple> getWarehouseStockQtyAndValue(Long warehouseId, List<Product> products);


	@Query("""
SELECT p.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM OrderItem i JOIN i.stockInfo s JOIN s.product p
WHERE p in (:products)
GROUP BY p.id
ORDER BY p.id
			""")
	List<Tuple> getOrderedQtyAndValue(List<Product> products);


	@Query("""
SELECT p.id, SUM(i.quantity), SUM(i.quantity * s.unitPrice)
FROM OrderItem i JOIN i.stockInfo s JOIN s.product p
WHERE
	p in (:products) AND
	s.warehouse.id = :warehouseId
GROUP BY p.id
ORDER BY p.id
			""")
	List<Tuple> getWarehouseOrderedQtyAndValue(Long warehouseId, List<Product> products);


	@Query("""
SELECT p.id, SUM(i.quantity)
FROM OfferItem i JOIN i.offer o JOIN i.product p
WHERE
	p in (:products) AND
	o.status = 'ACTIVE' AND
	o.validUntil >= CURRENT_DATE
GROUP BY p.id
ORDER BY p.id
			""")
	List<Tuple> getOfferedQty(List<Product> products);



}
