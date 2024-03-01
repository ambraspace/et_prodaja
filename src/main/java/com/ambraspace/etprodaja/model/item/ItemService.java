package com.ambraspace.etprodaja.model.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

@Service @Transactional
public class ItemService
{

	@Autowired
	private ItemRepository itemRepository;


	public List<Item> getItemsByOfferNo(String offerNo)
	{

		List<Item> items = new ArrayList<Item>();
		itemRepository.findByOfferIdOrderById(offerNo).forEach(items::add);
		return items;

	}


	public Item getItemByOfferOfferNoAndItemId(String offerNo, Long itemId)
	{
		return itemRepository.findByOfferIdAndId(offerNo, itemId).orElse(null);
	}


	public Item addItem(Offer offer, Item i)
	{

		i.setOffer(offer);
		i.setOrder(null);
		i.setDelivery(null);
		i.setDeliveryNote(null);

		return itemRepository.save(i);

	}


	public void deleteItem(String offerId, Long itemId)
	{

		Item item = getItemByOfferOfferNoAndItemId(offerId, itemId);

		if (item == null)
			throw new RuntimeException("Item not found in the database!");

		itemRepository.deleteByOfferIdAndId(offerId, itemId);

	}


	public Item updateItem(String offerNo, Long itemId, Item i)
	{

		Item fromRep = getItemByOfferOfferNoAndItemId(offerNo, itemId);

		if (fromRep == null)
			throw new RuntimeException("Item not found in the database!");

		fromRep.copyFieldsFrom(i);

		return itemRepository.save(fromRep);

	}


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

}
