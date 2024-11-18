package com.ambraspace.etprodaja.model.delivery;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.delivery.Delivery.Status;
import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItem;

@Service
public class DeliveryService
{

	@Autowired
	private DeliveryRepository deliveryRepository;


	public Delivery getDelivery(String id)
	{
		Delivery retVal = deliveryRepository.findById(id).orElse(null);
		fillTransientFields(List.of(retVal));
		return retVal;
	}


	public Page<Delivery> getDeliveries(Long supplierId, Status status, Pageable pageable)
	{

		Page<String> ids;

		if (supplierId == null)
		{
			if (status == null)
			{
				ids = deliveryRepository.findAllDeliveries(pageable);
			} else {
				ids = deliveryRepository.findByStatus(status, pageable);
			}
		} else {
			if (status == null)
			{
				ids = deliveryRepository.findBySupplierId(supplierId, pageable);
			} else {
				ids = deliveryRepository.findBySupplierIdAndStatus(supplierId, status, pageable);
			}
		}

		List<Delivery> selectedDeliveries = new ArrayList<Delivery>();
		deliveryRepository.getDeliveryData(ids.getContent(), pageable.getSort()).forEach(selectedDeliveries::add);
		fillTransientFields(selectedDeliveries);

		return new PageImpl<Delivery>(selectedDeliveries, pageable, ids.getTotalElements());

	}


	@Transactional
	public Delivery addDelivery(Delivery delivery)
	{

		delivery.setId(null);
		delivery.setStatus(Status.ON_THE_WAY);
		return deliveryRepository.save(delivery);

	}


	public Delivery updateDelivery(String deliveryId, Delivery delivery)
	{

		Delivery fromRep = getDelivery(deliveryId);

		if (fromRep == null)
			throw new RuntimeException("Delivery not found in the database!");

		fromRep.copyFieldsFrom(delivery);

		return deliveryRepository.save(fromRep);

	}


	public void deleteDelivery(String deliveryId)
	{

		Delivery fromRep = getDelivery(deliveryId);

		if (fromRep == null)
			throw new RuntimeException("Delivery not found in the database!");

		deliveryRepository.deleteById(deliveryId);

	}


	public Delivery setDelivered(String deliveryId)
	{

		Delivery fromRep = getDelivery(deliveryId);

		if (fromRep == null)
			throw new RuntimeException("Delivery not found in the database!");

		if (fromRep.getStatus().equals(Status.DELIVERED))
			throw new RuntimeException("Delivery has already been delivered!");

		fromRep.setStatus(Status.DELIVERED);

		return deliveryRepository.save(fromRep);

	}


	@Transactional
	public void deleteAllDeliveries()
	{

		List<Delivery> deliveries = getDeliveries(null, null, Pageable.unpaged()).getContent();

		deliveries.forEach(d -> deleteDelivery(d.getId()));

	}


	private void fillTransientFields(List<Delivery> deliveries)
	{

		for (Delivery d:deliveries)
		{

			BigDecimal value = BigDecimal.ZERO;

			if (d.getDeliveryItems() != null && d.getDeliveryItems().size() > 0)
			{

				for (DeliveryItem di : d.getDeliveryItems())
				{
					value = value.add(
							di.getOrderItem().getStockInfo().getUnitPrice()
							.multiply(di.getQuantity())
							.setScale(2, RoundingMode.HALF_EVEN));
				}

			}

			d.setValue(value);

		}

	}

}
