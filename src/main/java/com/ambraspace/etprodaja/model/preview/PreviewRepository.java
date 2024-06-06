package com.ambraspace.etprodaja.model.preview;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PreviewRepository extends CrudRepository<Preview, Long>, PagingAndSortingRepository<Preview, Long>
{

	Iterable<Preview> findByProductIsNull();

}
