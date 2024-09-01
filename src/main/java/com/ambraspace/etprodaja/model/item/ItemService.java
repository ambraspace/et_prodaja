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


	public Item getOrderItem(Long orderNo, Long itemId)
	{
		return itemRepository.findByOrderIdAndId(orderNo, itemId).orElse(null);
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
	public List<Item> addItems(String offerId, List<Item> items)
	{

		Offer offer = offerService.getOffer(offerId);

		if (offer == null)
			throw new RuntimeException("Offer not found in the database!");

		for (Item i:items)
		{
			i.setOffer(offer);
			i.setOrder(null);
		}

		List<Item> retVal = new ArrayList<Item>();

		itemRepository.saveAll(items).forEach(retVal::add);

		return retVal;

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
	public List<Item> updateItems(String offerNo, List<Item> items)
	{

		List<Long> ids = new ArrayList<Long>();

		items.stream().map(i -> i.getId()).forEach(ids::add);

		List<Item> fromRep = new ArrayList<Item>();

		itemRepository.findAllById(ids).forEach(fromRep::add);

		if (fromRep.size() != ids.size())
			throw new RuntimeException("Some items not found in the database! Cannot update.");

		for (Item i:fromRep)
		{
			Item updatedItem = items.stream().filter(it -> it.getId() == i.getId()).findFirst().orElseThrow();
			i.copyFieldsFrom(updatedItem);
		}

		List<Item> retVal = new ArrayList<Item>();

		itemRepository.saveAll(fromRep).forEach(retVal::add);

		return retVal;

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


	public BigDecimal getOfferedStockInfo(Long stockInfo)
	{
		return itemRepository.getOfferedStockInfoQty(stockInfo);
	}


	public BigDecimal getOrderedStockInfo(Long stockInfo)
	{
		return itemRepository.getOrderedStockInfoQty(stockInfo);
	}

}
