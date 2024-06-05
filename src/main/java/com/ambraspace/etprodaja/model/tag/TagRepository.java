package com.ambraspace.etprodaja.model.tag;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagRepository extends CrudRepository<Tag, String>, PagingAndSortingRepository<Tag, String>
{

	Iterable<Tag> findFirst10ByNameLikeIgnoreCase(String query);

}
