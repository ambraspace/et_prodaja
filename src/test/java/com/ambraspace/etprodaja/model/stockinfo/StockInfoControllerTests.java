package com.ambraspace.etprodaja.model.stockinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.ambraspace.etprodaja.model.category.Category;
import com.ambraspace.etprodaja.model.category.CategoryControllerTestComponent;
import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.company.CompanyControllerTestComponent;
import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.product.ProductControllerTestComponent;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
import com.ambraspace.etprodaja.model.warehouse.WarehouseControllerTestComponent;

@SpringBootTest
@AutoConfigureMockMvc
public class StockInfoControllerTests {

	@Autowired
	private SecurityTestComponent securityTestComponent;

	@Autowired
	private ProductControllerTestComponent productControllerTestComponent;

	@Autowired
	private StockInfoControllerTestComponent stockInfoControllerTestComponent;

	@Autowired
	private CategoryControllerTestComponent categoryControllerTestComponent;

	@Autowired
	private WarehouseControllerTestComponent warehouseControllerTestComponent;

	@Autowired
	private CompanyControllerTestComponent companyControllerTestComponent;


	@Test
	public void testStockInfoOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");

		List<Category> categories = categoryControllerTestComponent.saveCategories("""
[
	{
		"name":"Test category",
		"children":[]
	}
]
				""");

		Product product = productControllerTestComponent.addProduct(
				String.format("""
{
	"name":"Test decoration",
	"unit":"pcs.",
	"price":123.45,
	"category":
		{
			"id":%d
		},
	"tags":
		[
		],
	"comment":"This is a test"
}
						""", categories.get(0).getId()),
				0);

		Company company = companyControllerTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City"
}
				""");

		Warehouse warehouse = warehouseControllerTestComponent.addWarehouse(
				company.getId(),
				"""
{
	"name":"Test warehouse"
}
				""");

		StockInfo stockInfo = stockInfoControllerTestComponent.addStockInfo(
				product.getId(),
				String.format("""
{
	"warehouse":
	{
		"id":%d
	},
	"product":null,
	"customerReference":"CODE1234",
	"quantity":100,
	"unitPrice":3.14
}
				""", warehouse.getId()));

		assertNotEquals(stockInfo, null);

		stockInfo = stockInfoControllerTestComponent.getStockInfo(product.getId(), stockInfo.getId());

		assertNotEquals(stockInfo, null);

		assertNotEquals(
				stockInfoControllerTestComponent
				.getStockInfosByProduct(product.getId()).size(), 0);

		stockInfo = stockInfoControllerTestComponent.updateStockInfo(
				product.getId(),
				stockInfo.getId(),
				"""
{
	"warehouse":null,
	"product":null,
	"customerReference":"CODE4321",
	"quantity":1000,
	"unitPrice":6.28
}
				""");

		assertNotEquals(stockInfo, null);

		assertEquals(stockInfo.getCustomerReference(), "CODE4321");

		stockInfoControllerTestComponent.deleteStockInfo(product.getId(), stockInfo.getId());

		stockInfo = stockInfoControllerTestComponent.getStockInfo(product.getId(), stockInfo.getId());

		assertEquals(stockInfo, null);

		// Cleanup

		warehouseControllerTestComponent.deleteWarehouse(company.getId(), warehouse.getId());

		companyControllerTestComponent.deleteCompany(company.getId());

		productControllerTestComponent.deleteProduct(product.getId());

		categoryControllerTestComponent.saveCategories("[]");

	}

}
