package com.ambraspace.etprodaja.model.tag;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagRepository extends CrudRepository<Tag, Long>, PagingAndSortingRepository<Tag, Long>
{

	Iterable<Tag> findFirst10ByNameLikeIgnoreCase(String query);

}
