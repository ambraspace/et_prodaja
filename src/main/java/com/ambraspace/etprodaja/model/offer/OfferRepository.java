package com.ambraspace.etprodaja.model.offer;

import java.time.LocalDate;
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
	o.status = :status
			""")
	Page<String> findByUserUsernameAndCompanyIdAndStatus(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			@Param("status") Status status,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.company.id = :companyId AND
	o.status = :status AND
	o.validUntil < :today
			""")
	Page<String> findByUserUsernameAndCompanyIdAndStatusAndValidUntilIsBefore(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			@Param("status") Status status,
			@Param("today") LocalDate today,
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
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.company.id = :companyId AND
	o.validUntil < :today
			""")
	Page<String> findByUserUsernameAndCompanyIdAndValidUntilIsBefore(
			@Param("username") String username,
			@Param("companyId") Long companyId,
			@Param("today") LocalDate today,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.status = :status
			""")
	Page<String> findByUserUsernameAndStatus(
			@Param("username") String username,
			@Param("status") Status status,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.status = :status AND
	o.validUntil < :today
			""")
	Page<String> findByUserUsernameAndStatusAndValidUntilIsBefore(
			@Param("username") String username,
			@Param("status") Status status,
			@Param("today") LocalDate today,
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
SELECT
	o.id
FROM
	Offer o
WHERE
	o.user.username = :username AND
	o.validUntil < :today
			""")
	Page<String> findByUserUsernameAndValidUntilIsBefore(
			@Param("username") String username,
			@Param("today") LocalDate today,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.company.id = :companyId AND
	o.status = :status
			""")
	Page<String> findByCompanyIdAndStatus(
			@Param("companyId") Long companyId,
			@Param("status") Status status,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.company.id = :companyId AND
	o.status = :status AND
	o.validUntil < :today
			""")
	Page<String> findByCompanyIdAndStatusAndValidUntilIsBefore(
			@Param("companyId") Long companyId,
			@Param("status") Status status,
			@Param("today") LocalDate today,
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
SELECT
	o.id
FROM
	Offer o
WHERE
	o.company.id = :companyId AND
	o.validUntil < :today
			""")
	Page<String> findByCompanyIdAndValidUntilIsBefore(
			@Param("companyId") Long companyId,
			@Param("today") LocalDate today,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.status = :status
			""")
	Page<String> findByStatus(
			@Param("status") Status status,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.status = :status AND
	o.validUntil < :today
			""")
	Page<String> findByStatusAndValidUntilIsBefore(
			@Param("status") Status status,
			@Param("today") LocalDate today,
			Pageable pageable);


	@Query("""
SELECT
	o.id
FROM
	Offer o
WHERE
	o.validUntil < :today
			""")
	Page<String> findByValidUntilIsBefore(
			@Param("today") LocalDate today,
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
