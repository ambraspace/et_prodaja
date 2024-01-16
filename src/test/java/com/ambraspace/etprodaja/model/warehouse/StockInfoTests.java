package com.ambraspace.etprodaja.model.warehouse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.stockinfo.StockInfoService;

import jakarta.persistence.Tuple;

@SpringBootTest
public class StockInfoTests
{

	@Autowired
	private StockInfoService stockInfoService;

	@Test
	public void test1()
	{

		List<Product> products = new ArrayList<Product>();

		Product p = new Product();
		p.setId(1l);
		products.add(p);
		p = new Product();
		p.setId(2l);
		products.add(p);

		List<Tuple> retVal = this.stockInfoService.getStockInfoByProducts(products);

		System.out.println("Done");

	}

}
