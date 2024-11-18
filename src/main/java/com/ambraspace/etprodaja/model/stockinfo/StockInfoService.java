package com.ambraspace.etprodaja.model.stockinfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;

import jakarta.persistence.Tuple;

@Service
public class StockInfoService
{

	@Autowired
	private StockInfoRepository stockInfoRepository;


	public StockInfo getStockInfo(Long productId, Long id)
	{
		StockInfo retVal = stockInfoRepository.findByProductIdAndId(productId, id).orElse(null);
		if (retVal != null)
			fillTransientFields(List.of(retVal));
		return retVal;
	}


	public Page<StockInfo> getStockInfosByProduct(Long productId, Pageable pageable)
	{
		Page<StockInfo> retVal = stockInfoRepository.findByProductId(productId, pageable);
		fillTransientFields(retVal.getContent());
		return retVal;
	}


	public StockInfo addStockInfo(Long productId, StockInfo si)
	{
		Product p = new Product();
		p.setId(productId);
		si.setProduct(p);
		return stockInfoRepository.save(si);
	}


	@Transactional
	public StockInfo updateStockInfo(Long productId, Long i, StockInfo si)
	{

		StockInfo fromRep = getStockInfo(productId, i);

		if (fromRep == null)
			throw new RuntimeException("No such StockInfo object in the database!");

		fromRep.copyFieldsFrom(si);

		StockInfo retVal = stockInfoRepository.save(fromRep);

		fillTransientFields(List.of(retVal));

		return retVal;

	}


	@Transactional
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

	public List<Tuple> getStockInfoByWarehouseIdAndProducts(Long warehouseId, List<Product> products)
	{

		List<Tuple> retVal = new ArrayList<>();

		stockInfoRepository.getStockInfoByWarehouseIdAndProducts(warehouseId, products).forEach(retVal::add);

		return retVal;

	}

	public Warehouse getWarehousesByStockInfo(StockInfo stockInfo)
	{
		return stockInfoRepository.getWarehouseByStockInfo(stockInfo).orElse(null);
	}


	private void fillTransientFields(List<StockInfo> stockInfos)
	{

		List<Tuple> stockInfoOrderedQtys = new ArrayList<Tuple>();
		stockInfoRepository.getStockInfoOrderedQtys(stockInfos).forEach(stockInfoOrderedQtys::add);
		for (StockInfo si:stockInfos)
		{

			Tuple orderedQtyTuple = stockInfoOrderedQtys.stream().filter(t -> si.getId().equals(t.get(0, Long.class))).findFirst().orElse(null);

			BigDecimal orderedQyt = orderedQtyTuple != null ? orderedQtyTuple.get(1, BigDecimal.class) : BigDecimal.ZERO;

			si.setAvailableQuantity(si.getQuantity().subtract(orderedQyt));

		}

	}



}
