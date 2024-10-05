package com.ambraspace.etprodaja.model.delivery;

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

import com.ambraspace.etprodaja.model.delivery.Delivery.Status;

public interface DeliveryRepository extends CrudRepository<Delivery, Long>, PagingAndSortingRepository<Delivery, Long>
{

	@Query("SELECT d.id FROM Delivery d WHERE d.supplier.id = :companyId AND d.status = :status")
	Page<Long> findBySupplierIdAndStatus(@Param("companyId") Long companyId, @Param("status") Status status, Pageable pageable);


	@Query("SELECT d.id FROM Delivery d WHERE d.supplier.id = :companyId")
	Page<Long> findBySupplierId(Long companyId, Pageable pageable);


	@Query("SELECT d.id FROM Delivery d WHERE d.status = :status")
	Page<Long> findByStatus(Status status, Pageable pageable);


	@Query("SELECT d.id FROM Delivery d")
	Page<Long> findAllDeliveries(Pageable pageable);


	@Query("SELECT d FROM Delivery d WHERE d.id in (:ids)")
	@EntityGraph("delivery-with-details")
	Iterable<Delivery> getDeliveryData(@Param("ids") List<Long> ids, Sort sort);


	@Override
	@EntityGraph("delivery-with-details")
	Optional<Delivery> findById(Long id);

}
