package com.ambraspace.etprodaja.model.stockinfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.product.Product;

import jakarta.persistence.Tuple;

@Service @Transactional
public class StockInfoService
{

	@Autowired
	private StockInfoRepository stockInfoRepository;


	public StockInfo getStockInfo(Long productId, Long id)
	{
		return stockInfoRepository.findByProductIdAndId(productId, id).orElse(null);
	}


	public Page<StockInfo> getStockInfosByProduct(Long productId, Pageable pageable)
	{
		return stockInfoRepository.findByProductId(productId, pageable);
	}


	public StockInfo addStockInfo(Long productId, StockInfo si)
	{
		Product p = new Product();
		p.setId(productId);
		si.setProduct(p);
		return stockInfoRepository.save(si);
	}


	public StockInfo updateStockInfo(Long productId, Long i, StockInfo si)
	{

		StockInfo fromRep = getStockInfo(productId, i);

		if (fromRep == null)
			throw new RuntimeException("No such StockInfo object in the database!");

		fromRep.copyFieldsFrom(si);

		return stockInfoRepository.save(fromRep);

	}

	public void deleteStockInfo(Long productId, Long i)
	{

		StockInfo fromRep = getStockInfo(productId, i);

		if (fromRep == null)
			throw new RuntimeException("No such StockInfo object in the database!");

		stockInfoRepository.delete(fromRep);

	}

	public List<Tuple> getStockInfoByProducts(List<Product> products)
	{

		List<Tuple> retVal = new ArrayList<Tuple>();

		stockInfoRepository.getStockInfoByProducts(products).forEach(retVal::add);

		return retVal;

	}

	public List<StockInfo> getStockInfoByWarehouseIdAndProducts(Long warehouseId, List<Product> products)
	{

		List<StockInfo> retVal = new ArrayList<>();

		stockInfoRepository.findByWarehouseIdAndProductInOrderByProduct(warehouseId, products).forEach(retVal::add);

		return retVal;

	}

}
