package com.ambraspace.etprodaja.model.warehouse;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface WarehouseRepository extends CrudRepository<Warehouse, Long>, PagingAndSortingRepository<Warehouse, Long>
{

	Iterable<Warehouse> findByCompanyIdOrderByName(Long companyId);

	Optional<Warehouse> findByCompanyIdAndId(Long companyId, Long id);

}
