package com.ambraspace.etprodaja.model.offer;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.offer.Offer.Status;

public interface OfferRepository extends CrudRepository<Offer, String>, PagingAndSortingRepository<Offer, String>
{

	Page<Offer> findByUserUsernameAndCompanyIdAndStatus(String username, Long companyId, Status status, Pageable pageable);

	Page<Offer> findByUserUsernameAndCompanyIdAndStatusAndValidUntilIsBefore(String username, Long companyId, Status status, LocalDate today, Pageable pageable);

	Page<Offer> findByUserUsernameAndCompanyId(String username, Long companyId, Pageable pageable);

	Page<Offer> findByUserUsernameAndCompanyIdAndValidUntilIsBefore(String username, Long companyId, LocalDate today, Pageable pageable);

	Page<Offer> findByUserUsernameAndStatus(String username, Status status, Pageable pageable);

	Page<Offer> findByUserUsernameAndStatusAndValidUntilIsBefore(String username, Status status, LocalDate today, Pageable pageable);

	Page<Offer> findByUserUsername(String username, Pageable pageable);

	Page<Offer> findByUserUsernameAndValidUntilIsBefore(String username, LocalDate today, Pageable pageable);

	Page<Offer> findByCompanyIdAndStatus(Long companyId, Status status, Pageable pageable);

	Page<Offer> findByCompanyIdAndStatusAndValidUntilIsBefore(Long companyId, Status status, LocalDate today, Pageable pageable);

	Page<Offer> findByCompanyId(Long companyId, Pageable pageable);

	Page<Offer> findByCompanyIdAndValidUntilIsBefore(Long companyId, LocalDate today, Pageable pageable);

	Page<Offer> findByStatus(Status status, Pageable pageable);

	Page<Offer> findByStatusAndValidUntilIsBefore(Status status, LocalDate today, Pageable pageable);

	Page<Offer> findByValidUntilIsBefore(LocalDate today, Pageable pageable);

}
