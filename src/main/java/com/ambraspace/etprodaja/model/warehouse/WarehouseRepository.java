package com.ambraspace.etprodaja.model.warehouse;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface WarehouseRepository extends CrudRepository<Warehouse, Long>, PagingAndSortingRepository<Warehouse, Long>
{

	Iterable<Warehouse> findByCompanyIdOrderByName(Long companyId);

	Optional<Warehouse> findByCompanyIdAndId(Long companyId, Long id);

	@Override
	@EntityGraph("warehouse-with-company")
	Optional<Warehouse> findById(Long id);


	@EntityGraph("warehouse-with-company")
	@Query("""
SELECT w FROM Warehouse w
WHERE w.name LIKE :q OR w.company.name LIKE :q
ORDER BY w.name, w.company.name LIMIT :n
			""")
	Iterable<Warehouse> searchWarehouse(@Param("q") String query, @Param("n") Integer limit);

}
