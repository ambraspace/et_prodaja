package com.ambraspace.etprodaja.model.offer;

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

import com.ambraspace.etprodaja.model.offer.Offer.Status;

public interface OfferRepository extends CrudRepository<Offer, String>, PagingAndSortingRepository<Offer, String>
{

	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.company.id = :companyId AND
	o.status IN (:statuses)
			""")
	Page<String> findByUserUsernameAndCompanyIdAndStatus(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			@Param("statuses") List<Status> statuses,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.user.username = :username AND
	o.company.id = :companyId AND
	o.status IN (:statuses) AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByUserUsernameAndCompanyIdAndStatusAndProductId(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			@Param("statuses") List<Status> statuses,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.company.id = :companyId
			""")
	Page<String> findByUserUsernameAndCompanyId(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.user.username = :username AND
	o.company.id = :companyId AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByUserUsernameAndCompanyIdAndProductId(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.status IN (:statuses)
			""")
	Page<String> findByUserUsernameAndStatus(
			@Param("username") String username,
			@Param("statuses") List<Status> statuses,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.user.username = :username AND
	o.status IN (:statuses) AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByUserUsernameAndStatusAndProductId(
			@Param("username") String username,
			@Param("statuses") List<Status> statuses,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username
			""")
	Page<String> findByUserUsername(
			@Param("username") String username,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.user.username = :username AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByUserUsernameAndProductId(
			@Param("username") String username,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.company.id = :companyId AND
	o.status IN (:statuses)
			""")
	Page<String> findByCompanyIdAndStatus(
			@Param("companyId") Long companyId,
			@Param("statuses") List<Status> statuses,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.company.id = :companyId AND
	o.status IN (:statuses) AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByCompanyIdAndStatusAndProductId(
			@Param("companyId") Long companyId,
			@Param("statuses") List<Status> statuses,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.company.id = :companyId
			""")
	Page<String> findByCompanyId(
			@Param("companyId") Long companyId,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.company.id = :companyId AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByCompanyIdAndProductId(
			@Param("companyId") Long companyId,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.status IN (:statuses)
			""")
	Page<String> findByStatus(
			@Param("statuses") List<Status> statuses,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	o.status IN (:statuses) AND
	i.stockInfo.product.id = :product
			""")
	Page<String> findByStatusAndProductId(
			@Param("statuses") List<Status> statuses,
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT DISTINCT
	o.id
FROM
	Offer o JOIN o.items i
WHERE
	i.stockInfo.product.id = :product
			""")
	Page<String> findByProductId(
			@Param("product") Long productId,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
			""")
	Page<String> getAllOffers(Pageable pageable);


	@Query("SELECT o FROM Offer o WHERE o.id in (:ids)")
	@EntityGraph("offer-with-details")
	Iterable<Offer> getOfferDetails(@Param("ids") List<String> ids, Sort sort);


	@Override
	@EntityGraph("offer-with-details")
	Optional<Offer> findById(String id);


}
