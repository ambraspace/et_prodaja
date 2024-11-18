package com.ambraspace.etprodaja.model.offerItem;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OfferItemRepository extends CrudRepository<OfferItem, Long>, PagingAndSortingRepository<OfferItem, Long>
{

	@EntityGraph("offer-items")
	Optional<OfferItem> findByOfferIdAndId(String offerId, Long id);


	@EntityGraph("offer-items")
	Iterable<OfferItem> findByOfferId(String offerId);


	@Query("""
SELECT DISTINCT i.preview FROM OfferItem i
			""")
	Iterable<String> findItemPreviews();


	void deleteByOfferIdAndId(String offerId, Long id);


}
