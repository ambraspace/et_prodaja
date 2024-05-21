package com.ambraspace.etprodaja.model.item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItem;
import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.offer.OfferService;
import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

@Service
public class ItemService
{

	@Autowired
	private OfferService offerService;

	@Autowired
	private ItemRepository itemRepository;


	public Item getOfferItem(String offerNo, Long itemId)
	{
		return itemRepository.findByOfferIdAndId(offerNo, itemId).orElse(null);
	}


	public List<Item> getOfferItems(String offerNo)
	{
		List<Item> retVal = new ArrayList<>();
		itemRepository.findByOfferId(offerNo).forEach(retVal::add);
		return retVal;
	}


	public List<Item> getOrderItems(Long orderId, boolean onlyUndelivered)
	{

		List<Item> retVal = new ArrayList<>();
		if (onlyUndelivered)
		{
			itemRepository.findByOrderIdOnlyUndelivered(orderId).forEach(retVal::add);
		} else {
			itemRepository.findByOrderId(orderId).forEach(retVal::add);
		}

		for (Item i:retVal)
		{
			BigDecimal delivered = BigDecimal.ZERO;
			for (DeliveryItem di:i.getDeliveryItems())
			{
				delivered = delivered.add(di.getQuantity());
			}
			i.setOutstandingQuantity(i.getQuantity().subtract(delivered));
		}

		return retVal;

	}


	@Transactional
	public Item addItem(String offerId, Item i)
	{

		Offer offer = offerService.getOffer(offerId);

		if (offer == null)
			throw new RuntimeException("Offer not found in the database!");

		i.setOffer(offer);
		i.setOrder(null);

		return itemRepository.save(i);

	}


	@Transactional
	public void deleteItem(String offerId, Long itemId)
	{

		Item item = getOfferItem(offerId, itemId);

		if (item == null)
			throw new RuntimeException("Item not found in the database!");

		itemRepository.deleteByOfferIdAndId(offerId, itemId);

	}


	@Transactional
	public Item updateItem(String offerNo, Long itemId, Item i)
	{

		Item fromRep = getOfferItem(offerNo, itemId);

		if (fromRep == null)
			throw new RuntimeException("Item not found in the database!");

		fromRep.copyFieldsFrom(i);

		return itemRepository.save(fromRep);

	}


	@Transactional
	public List<Item> updateItems(List<Item> items)
	{
		List<Item> retVal = new ArrayList<Item>();
		itemRepository.saveAll(items).forEach(retVal::add);
		return retVal;
	}


	public List<Tuple> getItemDataForProductsInValidOffers(List<Product> products)
	{

		List<Tuple> retVal = new ArrayList<Tuple>();

		itemRepository.getItemDataForProductsInValidOffers(products).forEach(retVal::add);

		return retVal;

	}

	public List<Tuple> getItemDataForOrderedProducts(List<Product> products)
	{

		List<Tuple> retVal = new ArrayList<Tuple>();

		itemRepository.getItemDataForOrderedProducts(products).forEach(retVal::add);

		return retVal;

	}

	public List<Tuple> getItemDataForProductsInValidOffersByWarehouse(Long warehouseId, List<Product> products)
	{

		List<Tuple> retVal = new ArrayList<Tuple>();

		itemRepository.getItemDataForProductsInValidOffersByWarehouse(warehouseId, products).forEach(retVal::add);

		return retVal;

	}

	public List<Tuple> getItemDataForOrderedProductsByWarehouse(Long warehouseId, List<Product> products)
	{

		List<Tuple> retVal = new ArrayList<Tuple>();

		itemRepository.getItemDataForOrderedProductsByWarehouse(warehouseId, products).forEach(retVal::add);

		return retVal;

	}


	public List<Item> duplicateItems(List<Item> originalItems, Offer parentOffer)
	{

		List<Item> duplicatedItems = new ArrayList<>();

		originalItems.forEach(i -> {
			Item item = new Item();
			item.copyFieldsFrom(i);
			item.setOffer(parentOffer);
			item.setOrder(null);
			duplicatedItems.add(item);
		});

		List<Item> retVal = new ArrayList<>();

		itemRepository.saveAll(duplicatedItems).forEach(retVal::add);

		return retVal;

	}


}
