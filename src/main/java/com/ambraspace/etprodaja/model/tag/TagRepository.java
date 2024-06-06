package com.ambraspace.etprodaja.model.tag;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagRepository extends CrudRepository<Tag, String>, PagingAndSortingRepository<Tag, String>
{

	Iterable<Tag> findFirst10ByNameLikeIgnoreCase(String query);

	@Query("""
SELECT DISTINCT t FROM Product p JOIN p.tags t
			""")
	Iterable<Tag> getAssignedTags();

}
