package com.ambraspace.etprodaja.model.delivery;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.delivery.Delivery.Status;

public interface DeliveryRepository extends CrudRepository<Delivery, Long>, PagingAndSortingRepository<Delivery, Long>
{

	@EntityGraph("delivery-with-details")
	Page<Delivery> findBySupplierIdAndStatus(Long companyId, Status status, Pageable pageable);


	@EntityGraph("delivery-with-details")
	Page<Delivery> findBySupplierId(Long companyId, Pageable pageable);


	@EntityGraph("delivery-with-details")
	Page<Delivery> findByStatus(Status status, Pageable pageable);


	@Override
	@EntityGraph("delivery-with-details")
	Page<Delivery> findAll(Pageable pageable);


	@Override
	@EntityGraph("delivery-with-details")
	Optional<Delivery> findById(Long id);

}
