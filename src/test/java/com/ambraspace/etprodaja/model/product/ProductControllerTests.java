package com.ambraspace.etprodaja.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.ambraspace.etprodaja.model.category.Category;
import com.ambraspace.etprodaja.model.category.CategoryControllerTestComponent;
import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.company.CompanyControllerTestComponent;
import com.ambraspace.etprodaja.model.preview.Preview;
import com.ambraspace.etprodaja.model.preview.PreviewControllerTestComponent;
import com.ambraspace.etprodaja.model.preview.PreviewService;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;
import com.ambraspace.etprodaja.model.stockinfo.StockInfoControllerTestComponent;
import com.ambraspace.etprodaja.model.tag.Tag;
import com.ambraspace.etprodaja.model.tag.TagControllerTestComponent;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
import com.ambraspace.etprodaja.model.warehouse.WarehouseControllerTestComponent;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

	@Autowired
	private SecurityTestComponent securityTestComponent;

	@Autowired
	private ProductControllerTestComponent productControllerTestComponent;

	@Autowired
	private CategoryControllerTestComponent categoryControllerTestComponent;

	@Autowired
	private TagControllerTestComponent tagControllerTestComponent;

	@Autowired
	private CompanyControllerTestComponent companyControllerTestComponent;

	@Autowired
	private WarehouseControllerTestComponent warehouseControllerTestComponent;

	@Autowired
	private StockInfoControllerTestComponent stockInfoControllerTestComponent;

	@Autowired
	private PreviewControllerTestComponent previewControllerTestComponent;

	@Autowired
	private PreviewService previewService;


	@Value("${et-prodaja.storage-location}")
	private String storageLocation;


	@Test
	public void testProductOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		List<Category> categories = categoryControllerTestComponent.saveCategories("""
 [
	{
		"name":"Test category",
		"children":[]
	},
	{
		"name":"Another category",
		"children":[]
	}
 ]
				""");

		Tag tag = tagControllerTestComponent.addTag("""
 {
	"name":"Test tag",
	"color":"#111111"
 }
				""");


		List<Preview> previews = previewControllerTestComponent.addPreviews(3);

		StringBuilder previewArray = new StringBuilder();
		previewArray.append("[");
		for (int i = 0; i < previews.size(); i++)
		{
			Preview pr = previews.get(i);
			previewArray.append(String.format("""
{
		"id":%d,
		"fileName":"%s",
		"originalFileName":"%s",
		"size":%d,
		"primary":false
}
									""", pr.getId(), pr.getFileName(), pr.getOriginalFileName(), pr.getSize()));
			if (i < previews.size() - 1)
				previewArray.append(",");
		}

		previewArray.append("]");

		String productBody = String.format("""
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
			{
				"name":"%s"
			}
		],
	"previews":%s,
	"comment":"This is a test"
}
				""", categories.get(0).getId(), tag.getName(), previewArray.toString());

		Product product = productControllerTestComponent.addProduct(productBody);

		assertNotEquals(product, null);

		assertEquals(product.getCategory().getId(), categories.get(0).getId());

		assertEquals(product.getTags().get(0).getName(), tag.getName());

		assertEquals(product.getPreviews().size(), previews.size());

		assertEquals(product.getPrice().compareTo(BigDecimal.valueOf(12345, 2)), 0);

		for (Preview preview:product.getPreviews())
		{
			assertTrue(new File(storageLocation, preview.getFileName()).exists());
		}

		byte[] fileImage = previewControllerTestComponent.downloadProductPreview(product.getPreviews().get(0).getFileName());

		assertEquals(fileImage.length, 1);

		previews.addAll(previewControllerTestComponent.addPreviews(1));

		previewArray.setLength(0);
		previewArray.append("[");

		for (int i = 1; i < previews.size(); i++)
		{
			Preview pr = previews.get(i);
			previewArray.append(String.format("""
					{
							"id":%d,
							"fileName":"%s",
							"originalFileName":"%s",
							"size":%d,
							"primary":false
					}
									""", pr.getId(), pr.getFileName(), pr.getOriginalFileName(), pr.getSize()));
			if (i < previews.size() - 1)
				previewArray.append(",");
		}

		previewArray.append("]");


		String updatedProductBody = String.format("""
{
	"id":%d,
	"name":"Updated test decoration",
	"unit":"pcs.",
	"price":234.56,
	"category":
		{
			"id":%d
		},
	"tags":
		[
		],
	"comment":"This is an updated test",
	"previews":%s
}
				""",
				product.getId(),
				categories.get(1).getId(),
				previewArray.toString());

		Product updatedProduct =
				productControllerTestComponent.updateProduct(product.getId(), updatedProductBody);

		assertEquals(updatedProduct.getCategory().getId(), categories.get(1).getId());

		assertEquals(updatedProduct.getTags().size(), 0);

		assertEquals(updatedProduct.getId(), product.getId());

		assertEquals(updatedProduct.getPreviews().size(), 3);

		for (Preview preview:updatedProduct.getPreviews())
		{
			assertTrue(new File(storageLocation, preview.getFileName()).exists());
		}

		assertEquals(updatedProduct.getPreviews().get(0).getId(), previews.get(1).getId());

		assertEquals(updatedProduct.getPreviews().get(2).getId(), previews.get(3).getId());


		Company company = companyControllerTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City or country"
}
				""");

		Warehouse warehouse = warehouseControllerTestComponent.addWarehouse(company.getId(), """
{
	"name":"Test warehouse"
}
				""");

		StockInfo stockInfo = stockInfoControllerTestComponent.addStockInfo(product.getId(), String.format("""
{
	"warehouse":
	{
		"id":%d
	},
	"customerReference":"CODE1234",
	"quantity":10,
	"unitPrice":100.00
}
				""", warehouse.getId()));


		// Fails due to referential integrity
		assertThrows(AssertionError.class, () -> {
			productControllerTestComponent.deleteProduct(updatedProduct.getId());
		});

		stockInfoControllerTestComponent.deleteStockInfo(product.getId(), stockInfo.getId());

		warehouseControllerTestComponent.deleteWarehouse(company.getId(), warehouse.getId());

		companyControllerTestComponent.deleteCompany(company.getId());

		productControllerTestComponent.deleteProduct(updatedProduct.getId());

		for (Preview preview:previews)
		{
			assertTrue(new File(storageLocation, preview.getFileName()).exists());
		}

		previewService.deleteOrphanPreviews();

		for (Preview preview:previews)
		{
			assertFalse(new File(storageLocation, preview.getFileName()).exists());
		}

		tagControllerTestComponent.deleteTag(tag.getName());

		categoryControllerTestComponent.saveCategories("[]");

	}


	@Test
	public void testProductSearch() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");

		List<Category> categories = categoryControllerTestComponent.saveCategories("""
[
	{
		"name":"Kat 1",
		"children":[]
	},
	{
		"name":"Kat 2",
		"children":[]
	}
]
				""");

		tagControllerTestComponent.addTag("""
{
	"name":"Tag 1",
	"color":"#111111"
}
				""");

		tagControllerTestComponent.addTag("""
{
	"name":"Tag 2",
	"color":"#222222"
}
				""");

		List<Tag> tags = tagControllerTestComponent.getTags();

		Company company = companyControllerTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City or country"
}
				""");

		warehouseControllerTestComponent.addWarehouse(company.getId(), """
{
	"name":"Warehouse 1"
}
				""");

		warehouseControllerTestComponent.addWarehouse(company.getId(), """
{
	"name":"Warehouse 2"
}
				""");

		List<Warehouse> warehouses = warehouseControllerTestComponent.getWarehouses(company.getId());

		String productTemplate = """
				{
				"name":"%s",
				"comment":"%s",
				"unit":"pcs.",
				"price":%.2f,
				"category":
				{
				"id":%d
				},
				"tags":
				[
				{
				"name":"%s"
				}
				]
				}
				""";

		List<Product> products = new ArrayList<Product>();

		products.add(productControllerTestComponent.addProduct(String.format(productTemplate,
				"Product a bc",
				"Comment def",
				123.45,
				categories.get(0).getId(),
				tags.get(0).getName())));

		products.add(productControllerTestComponent.addProduct(String.format(productTemplate,
				"Product A BC",
				"Comment DEF",
				111.11,
				categories.get(1).getId(),
				tags.get(0).getName())));

		products.add(productControllerTestComponent.addProduct(String.format(productTemplate,
				"Product ghi",
				"Comment jkl",
				200.00,
				categories.get(0).getId(),
				tags.get(1).getName())));

		products.add(productControllerTestComponent.addProduct(String.format(productTemplate,
				"Product GHI",
				"Comment JKL",
				133.33,
				categories.get(1).getId(),
				tags.get(1).getName())));


		List<StockInfo> stockInfos = new ArrayList<StockInfo>();

		stockInfos.add(stockInfoControllerTestComponent.addStockInfo(products.get(0).getId(), String.format("""
				{
				"warehouse":
				{
					"id":%d
				},
				"customerReference":"CODE1234",
				"quantity":10,
				"unitPrice":100.00
			}
							""", warehouses.get(0).getId())));

		stockInfos.add(stockInfoControllerTestComponent.addStockInfo(products.get(1).getId(), String.format("""
				{
				"warehouse":
				{
					"id":%d
				},
				"customerReference":"CODE1234",
				"quantity":10,
				"unitPrice":100.00
			}
							""", warehouses.get(1).getId())));

		stockInfos.add(stockInfoControllerTestComponent.addStockInfo(products.get(2).getId(), String.format("""
				{
				"warehouse":
				{
					"id":%d
				},
				"customerReference":"CODE1234",
				"quantity":10,
				"unitPrice":100.00
			}
							""", warehouses.get(0).getId())));

		stockInfos.add(stockInfoControllerTestComponent.addStockInfo(products.get(3).getId(), String.format("""
				{
				"warehouse":
				{
					"id":%d
				},
				"customerReference":"CODE1234",
				"quantity":10,
				"unitPrice":100.00
			}
							""", warehouses.get(1).getId())));

		String p1a = "a bc";

		String p1b = "def";

		Boolean p2 = true;

		Long p3 = warehouses.get(0).getId();

		List<String> p4a = List.of(tags.get(0).getName());

		List<String> p4b = tags.stream().map(t -> t.getName()).collect(Collectors.toList());

		Long p5 = categories.get(1).getId();

		assertEquals(productControllerTestComponent
				.getProducts(null, null, null, null, null).size(), 4);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, null, null, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, null, null, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, p3, null, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, null, p4a, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, null, p4b, null).size(), 4);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, null, null, p5).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, p3, null, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, null, p4a, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, null, p4b, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, null, null, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, p3, null, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, null, p4a, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, null, p4b, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, null, null, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, p3, p4a, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, p3, p4b, null).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, p3, null, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, null, p4a, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, null, p4b, p5).size(), 2);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, p3, p4a, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, p3, p4a, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, p3, p4b, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, p3, p4b, null).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, p3, null, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, p3, null, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, null, p4a, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, null, p4a, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, null, p4b, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, null, p4b, p5).size(), 1);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, p3, p4a, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(null, null, p3, p4b, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, p3, p4a, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, p3, p4a, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(p1a, null, p3, p4b, p5).size(), 0);

		assertEquals(productControllerTestComponent
				.getProducts(p1b, p2, p3, p4b, p5).size(), 0);

		products = productControllerTestComponent.getProducts(null, null, null, null, null);

		assertEquals(products.get(0).getAvailableQty().compareTo(BigDecimal.valueOf(10)), 0);

		assertEquals(products.get(0).getOfferedQty().compareTo(BigDecimal.valueOf(0)), 0);

		assertEquals(products.get(0).getOrderedQty().compareTo(BigDecimal.valueOf(0)), 0);

		assertEquals(products.get(0).getPurchasePrice().compareTo(BigDecimal.valueOf(100)), 0);

		stockInfos.add(stockInfoControllerTestComponent.addStockInfo(products.get(0).getId(), String.format("""
				{
				"warehouse":
				{
					"id":%d
				},
				"customerReference":"CODE1234",
				"quantity":5,
				"unitPrice":90.00
			}
							""", warehouses.get(1).getId())));


		Product product = productControllerTestComponent.getProduct(products.get(0).getId());

		assertNotEquals(product, null);

		assertEquals(product.getAvailableQty().compareTo(BigDecimal.valueOf(15)), 0);

		assertEquals(product.getOfferedQty().compareTo(BigDecimal.valueOf(0)), 0);

		assertEquals(product.getOrderedQty().compareTo(BigDecimal.valueOf(0)), 0);

		assertEquals(product.getPurchasePrice().compareTo(BigDecimal.valueOf(9667, 2)), 0);

		stockInfoControllerTestComponent.deleteStockInfo(products.get(0).getId(), stockInfos.get(4).getId());
		stockInfoControllerTestComponent.deleteStockInfo(products.get(0).getId(), stockInfos.get(0).getId());
		stockInfoControllerTestComponent.deleteStockInfo(products.get(1).getId(), stockInfos.get(1).getId());
		stockInfoControllerTestComponent.deleteStockInfo(products.get(2).getId(), stockInfos.get(2).getId());
		stockInfoControllerTestComponent.deleteStockInfo(products.get(3).getId(), stockInfos.get(3).getId());

		productControllerTestComponent.deleteProduct(products.get(0).getId());
		productControllerTestComponent.deleteProduct(products.get(1).getId());
		productControllerTestComponent.deleteProduct(products.get(2).getId());
		productControllerTestComponent.deleteProduct(products.get(3).getId());

		warehouseControllerTestComponent.deleteWarehouse(company.getId(), warehouses.get(0).getId());
		warehouseControllerTestComponent.deleteWarehouse(company.getId(), warehouses.get(1).getId());

		companyControllerTestComponent.deleteCompany(company.getId());

		tagControllerTestComponent.deleteTag(tags.get(0).getName());
		tagControllerTestComponent.deleteTag(tags.get(1).getName());

		categoryControllerTestComponent.saveCategories("[]");

	}

}
