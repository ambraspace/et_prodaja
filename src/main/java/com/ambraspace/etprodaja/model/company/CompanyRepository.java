package com.ambraspace.etprodaja.model.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CompanyRepository extends CrudRepository<Company, Long>, PagingAndSortingRepository<Company, Long>
{

	Page<Company> findByNameIsLikeIgnoreCase(String query, Pageable pageable);

}
