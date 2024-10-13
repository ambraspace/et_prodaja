package com.ambraspace.etprodaja.model.deliveryItem;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface DeliveryItemRepository extends CrudRepository<DeliveryItem, Long>, PagingAndSortingRepository<DeliveryItem, Long>
{

	@EntityGraph("deliveryItem")
	Iterable<DeliveryItem> findByDeliveryId(String deliveryId);


	@EntityGraph("deliveryItem-with-details")
	Optional<DeliveryItem> findByDeliveryIdAndId(String deliveryId, Long id);

}
