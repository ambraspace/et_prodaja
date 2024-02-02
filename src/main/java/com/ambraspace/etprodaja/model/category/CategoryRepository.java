package com.ambraspace.etprodaja.model.category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends CrudRepository<Category, Long>, PagingAndSortingRepository<Category, Long>
{

	Iterable<Category> findByRootCategoryIsTrue();

}