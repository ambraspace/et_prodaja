package com.ambraspace.etprodaja.model.offer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

@Service
public class OfferService
{

	@Autowired
	private ItemRepository itemRepository;


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
