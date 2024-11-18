package com.ambraspace.etprodaja.model.offerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.offer.OfferService;

@Service
public class OfferItemService
{

	@Autowired
	private OfferService offerService;

	@Autowired
	private OfferItemRepository offerItemRepository;


	@Transactional(readOnly = true)
	public OfferItem getOfferItem(String offerNo, Long itemId)
	{
		return offerItemRepository.findByOfferIdAndId(offerNo, itemId).orElse(null);
	}


	@Transactional(readOnly = true)
	public List<OfferItem> getOfferItems(String offerNo)
	{
		List<OfferItem> retVal = new ArrayList<>();
		offerItemRepository.findByOfferId(offerNo).forEach(retVal::add);
		return retVal;
	}


	@Transactional
	public List<OfferItem> addItems(String offerId, List<OfferItem> items)
	{

		Offer offer = offerService.getOffer(offerId);

		if (offer == null)
			throw new RuntimeException("Offer not found in the database!");

		for (OfferItem i:items)
		{
			i.setOffer(offer);
		}

		List<OfferItem> retVal = new ArrayList<OfferItem>();

		offerItemRepository.saveAll(items).forEach(retVal::add);

		return retVal;

	}


	@Transactional
	public void deleteItem(String offerId, Long itemId)
	{

		OfferItem item = getOfferItem(offerId, itemId);

		if (item == null)
			throw new RuntimeException("Item not found in the database!");

		offerItemRepository.deleteByOfferIdAndId(offerId, itemId);

	}


	@Transactional
	public List<OfferItem> updateItems(String offerNo, List<OfferItem> items)
	{

		Set<Long> ids = items.stream().map(i -> i.getId()).collect(Collectors.toSet());

		List<OfferItem> fromRep = new ArrayList<OfferItem>();

		offerItemRepository.findAllById(ids).forEach(fromRep::add);

		if (fromRep.size() < ids.size())
			throw new RuntimeException("Some items not found in the database! Cannot update.");

		for (OfferItem i:fromRep)
		{
			int itemIndex = items.indexOf(i);
			if (itemIndex < 0)
				throw new RuntimeException("Item not found in returned list!");
			i.copyFieldsFrom(items.get(itemIndex));
		}

		List<OfferItem> retVal = new ArrayList<OfferItem>();

		offerItemRepository.saveAll(fromRep).forEach(retVal::add);

		return retVal;

	}


//	@Transactional
//	public List<OfferItem> updateItems(List<OfferItem> items)
//	{
//		List<OfferItem> retVal = new ArrayList<OfferItem>();
//		offerItemRepository.saveAll(items).forEach(retVal::add);
//		return retVal;
//	}
//
//
//	public List<Tuple> getOfferedQtys(List<Product> products)
//	{
//		List<Tuple> retVal = new ArrayList<Tuple>();
//		offerItemRepository.getOfferedQtys(products).forEach(retVal::add);
//		return retVal;
//	}
//
//
//	public List<Tuple> getItemDataForProductsInValidOffers(List<Product> products)
//	{
//
//		List<Tuple> retVal = new ArrayList<Tuple>();
//
//		offerItemRepository.getItemDataForProductsInValidOffers(products).forEach(retVal::add);
//
//		return retVal;
//
//	}
//
//	public List<Tuple> getItemDataForOrderedProducts(List<Product> products)
//	{
//
//		List<Tuple> retVal = new ArrayList<Tuple>();
//
//		offerItemRepository.getItemDataForOrderedProducts(products).forEach(retVal::add);
//
//		return retVal;
//
//	}
//
//	public List<Tuple> getItemDataForProductsInValidOffersByWarehouse(Long warehouseId, List<Product> products)
//	{
//
//		List<Tuple> retVal = new ArrayList<Tuple>();
//
//		offerItemRepository.getItemDataForProductsInValidOffersByWarehouse(warehouseId, products).forEach(retVal::add);
//
//		return retVal;
//
//	}
//
//	public List<Tuple> getItemDataForOrderedProductsByWarehouse(Long warehouseId, List<Product> products)
//	{
//
//		List<Tuple> retVal = new ArrayList<Tuple>();
//
//		offerItemRepository.getItemDataForOrderedProductsByWarehouse(warehouseId, products).forEach(retVal::add);
//
//		return retVal;
//
//	}
//
//
	public List<OfferItem> duplicateItems(List<OfferItem> originalItems, Offer parentOffer)
	{

		List<OfferItem> duplicatedItems = new ArrayList<>();

		originalItems.forEach(i -> {
			OfferItem item = new OfferItem();
			item.copyFieldsFrom(i);
			item.setOffer(parentOffer);
			duplicatedItems.add(item);
		});

		List<OfferItem> retVal = new ArrayList<>();

		offerItemRepository.saveAll(duplicatedItems).forEach(retVal::add);

		return retVal;

	}


	public List<String> getItemPreviews()
	{
		List<String> retVal = new ArrayList<String>();
		offerItemRepository.findItemPreviews().forEach(retVal::add);
		return retVal;
	}



//	public void fillStockInfoTransientFields(List<StockInfo> stockInfos)
//	{
//
//		for (StockInfo si:stockInfos)
//		{
//			BigDecimal offeredQty = getOfferedStockInfo(si.getId());
//			BigDecimal orderedQty = getOrderedStockInfo(si.getId());
//			si.setAvailableQuantity(
//					si.getQuantity()
//						.subtract(offeredQty == null ? BigDecimal.ZERO : offeredQty)
//						.subtract(orderedQty == null ? BigDecimal.ZERO : orderedQty)
//			);
//		}
//
//	}

}
