package com.ambraspace.etprodaja.model.company;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CompanyRepository extends CrudRepository<Company, Long>, PagingAndSortingRepository<Company, Long>
{


}
