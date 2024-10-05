package com.ambraspace.etprodaja.model.deliveryItem;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.delivery.Delivery;
import com.ambraspace.etprodaja.model.delivery.DeliveryService;

@Service
public class DeliveryItemService
{

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private DeliveryItemRepository deliveryItemRepository;


	public DeliveryItem getDeliveryItem(Long deliveryId, Long id)
	{
		return deliveryItemRepository.findByDeliveryIdAndId(deliveryId, id).orElse(null);
	}


	public List<DeliveryItem> getDeliveryItems(Long deliveryId)
	{

		Delivery delivery = deliveryService.getDelivery(deliveryId);

		if (delivery == null)
			throw new RuntimeException("No such delivery in the database!");

		List<DeliveryItem> retVal = new ArrayList<DeliveryItem>();

		deliveryItemRepository.findByDeliveryId(deliveryId).forEach(retVal::add);

		return retVal;

	}


	@Transactional
	public List<DeliveryItem> addDeliveryItems(Long deliveryId, List<DeliveryItem> dis)
	{

		Delivery delivery = deliveryService.getDelivery(deliveryId);

		if (delivery == null)
			throw new RuntimeException("No such delivery in the database!");

		for (DeliveryItem di:dis)
		{
			di.setDelivery(delivery);
		}

		List<DeliveryItem> saved = new ArrayList<DeliveryItem>();

		deliveryItemRepository.saveAll(dis).forEach(saved::add);

		return saved;

	}


	@Transactional
	public DeliveryItem updateDeliveryItem(Long deliveryId, Long id, DeliveryItem di)
	{

		DeliveryItem fromRep = getDeliveryItem(deliveryId, id);

		if (fromRep == null)
			throw new RuntimeException("No such DeliveryItem in the database!");

		fromRep.copyFieldsFrom(di);

		return deliveryItemRepository.save(fromRep);

	}


	@Transactional
	public void deleteDeliveryItem(Long deliveryId, Long id)
	{

		DeliveryItem fromRep = getDeliveryItem(deliveryId, id);

		if (fromRep == null)
			throw new RuntimeException("No such DeliveryItem in the database!");

		deliveryItemRepository.delete(fromRep);

	}

}
