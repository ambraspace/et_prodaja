package com.ambraspace.etprodaja.model.category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer>
{

	Iterable<Category> findByRootCategoryIsTrue();

}