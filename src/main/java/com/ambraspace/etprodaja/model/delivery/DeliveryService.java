package com.ambraspace.etprodaja.model.delivery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.delivery.Delivery.Status;
import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemService;

@Service
public class DeliveryService
{

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Autowired
	private ItemService itemService;


	public Delivery getDelivery(Long id)
	{
		return deliveryRepository.findById(id).orElse(null);
	}


	public Page<Delivery> getDeliveries(Long supplierId, Status status, Pageable pageable)
	{
		if (supplierId == null)
		{
			if (status == null)
			{
				return deliveryRepository.findAll(pageable);
			} else {
				return deliveryRepository.findByStatus(status, pageable);
			}
		} else {
			if (status == null)
			{
				return deliveryRepository.findBySupplierId(supplierId, pageable);
			} else {
				return deliveryRepository.findBySupplierIdAndStatus(supplierId, status, pageable);
			}
		}
	}


	@Transactional
	public Delivery addDelivery(Delivery delivery)
	{

		delivery.setId(null);
		delivery.setStatus(Status.ON_THE_WAY);

		if (delivery.getItems() == null || delivery.getItems().size() == 0)
			throw new RuntimeException("Items not supplied!");

		Delivery saved = deliveryRepository.save(delivery);

		List<Item> fromRep = new ArrayList<Item>();

		delivery.getItems().forEach(i -> {
			Item item = itemService.getItem(i.getId());
			if (item == null)
				throw new RuntimeException("Item not found in the database!");
			item.setDelivery(saved);
			if (i.getDeliveryNote() != null && (!i.getDeliveryNote().isBlank()))
				item.setDeliveryNote(i.getDeliveryNote());
			fromRep.add(item);
		});

		itemService.updateItems(fromRep);

		/*
		 * TODO: Da li ovdje vratiti kompletan EntityGraph?
		 */

		return saved;

	}


	public Delivery updateDelivery(Long deliveryId, Delivery delivery)
	{

		Delivery fromRep = getDelivery(deliveryId);

		if (fromRep == null)
			throw new RuntimeException("Delivery not found in the database!");

		fromRep.copyFieldsFrom(delivery);

		return deliveryRepository.save(fromRep);

	}


	public void deleteDelivery(Long deliveryId)
	{

		Delivery fromRep = getDelivery(deliveryId);

		if (fromRep == null)
			throw new RuntimeException("Delivery not found in the database!");

		List<Item> updatedItems = new ArrayList<Item>();

		fromRep.getItems().forEach(i -> {
			i.setDeliveryNote(null);
			i.setDelivery(null);
			updatedItems.add(i);
		});

		itemService.updateItems(updatedItems);

		deliveryRepository.deleteById(deliveryId);

	}


	public Delivery setDelivered(Long deliveryId)
	{

		Delivery fromRep = getDelivery(deliveryId);

		if (fromRep == null)
			throw new RuntimeException("Delivery not found in the database!");

		if (fromRep.getStatus().equals(Status.DELIVERED))
			throw new RuntimeException("Delivery has already been delivered!");

		fromRep.setDeliveryDate(LocalDate.now());
		fromRep.setStatus(Status.DELIVERED);

		return deliveryRepository.save(fromRep);

	}


	@Transactional
	public void deleteAllDeliveries()
	{

		List<Delivery> deliveries = getDeliveries(null, null, Pageable.unpaged()).getContent();

		deliveries.forEach(d -> deleteDelivery(d.getId()));

	}

}
