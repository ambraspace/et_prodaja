package com.ambraspace.etprodaja.model.offer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.ambraspace.etprodaja.model.delivery.Delivery;
import com.ambraspace.etprodaja.model.delivery.DeliveryControllerTestComponent;
import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItem;
import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItemControllerTestComponent;
import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemControllerTestComponent;
import com.ambraspace.etprodaja.model.order.Order;
import com.ambraspace.etprodaja.model.order.Order.Status;
import com.ambraspace.etprodaja.model.order.OrderControllerTestComponent;
import com.ambraspace.etprodaja.model.preview.Preview;
import com.ambraspace.etprodaja.model.preview.PreviewControllerTestComponent;
import com.ambraspace.etprodaja.model.preview.PreviewService;
import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.product.ProductControllerTestComponent;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;
import com.ambraspace.etprodaja.model.stockinfo.StockInfoControllerTestComponent;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
import com.ambraspace.etprodaja.model.warehouse.WarehouseControllerTestComponent;

@SpringBootTest
@AutoConfigureMockMvc
public class OfferWorkflowTests
{

	@Autowired
	private SecurityTestComponent securityTestComponent;

	@Autowired
	private CompanyControllerTestComponent companyControllerTestComponent;

	@Autowired
	private WarehouseControllerTestComponent warehouseControllerTestComponent;

	@Autowired
	private CategoryControllerTestComponent categoryControllerTestComponent;

	@Autowired
	private PreviewControllerTestComponent previewControllerTestComponent;

	@Autowired
	private ProductControllerTestComponent productControllerTestComponent;

	@Autowired
	private StockInfoControllerTestComponent stockInfoControllerTestComponent;

	@Autowired
	private OfferControllerTestComponent offerControllerTestComponent;

	@Autowired
	private ItemControllerTestComponent itemControllerTestComponent;

	@Autowired
	private OrderControllerTestComponent orderControllerTestComponent;

	@Autowired
	private DeliveryControllerTestComponent deliveryControllerTestComponent;

	@Autowired
	private DeliveryItemControllerTestComponent deliveryItemControllerTestComponent;

	@Autowired
	private PreviewService previewService;


	@Test
	public void testOfferOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		List<Company> companies = new ArrayList<Company>();

		List<Warehouse> warehouses = new ArrayList<Warehouse>();

		List<Category> categories = new ArrayList<Category>();

		List<Product> products = new ArrayList<Product>();

		List<StockInfo> stockInfos = new ArrayList<StockInfo>();


		// Preparation

		addCompanies(companies);

		addWarehouses(companies, warehouses);

		addCategories(categories);

		addProducts(categories, products);

		addStockInfos(products, warehouses, stockInfos);


		// Testing (long one - follow comments)

		List<Offer> offers = new ArrayList<Offer>();

		// First offer with two items

		offers.add(offerControllerTestComponent.addOffer(String.format("""
{
	"validUntil":"%s",
	"company":{"id":%d},
	"vat":17.00,
	"notes":"Lead time: 10 days",
	"comments":"",
	"status":"ACTIVE"
}
							""", ZonedDateTime.of(LocalDate.now().plusDays(7), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT), companies.get(0).getId()), "admin"));

		String itemTemplate = """
				{
				"stockInfo":{"id":%d},
				"productName":"%s",
				"quantity":%.2f,
				"grossPrice":%.2f,
				"discountPercent":%.2f
			}
		""";

		String itemBody = "[";
		itemBody = itemBody + String.format(itemTemplate, stockInfos.get(0).getId(), products.get(0).getName(), 5.0, products.get(0).getPrice(), 11.00) + ",";
		itemBody = itemBody + String.format(itemTemplate, stockInfos.get(1).getId(), products.get(0).getName(), 5.0, products.get(0).getPrice(), 11.00) + "]";

		itemControllerTestComponent.addItems(offers.get(0).getId(), itemBody);

		// Verify that transient fields are correctly calculated

		offers.set(0, offerControllerTestComponent.getOffer(offers.get(0).getId()));

		assertEquals(offers.get(0).getCost().compareTo(BigDecimal.valueOf(975.0)), 0);

		assertEquals(offers.get(0).getValue().compareTo(BigDecimal.valueOf(988.9)), 0);

		assertEquals(offers.get(0).getMargin().compareTo(BigDecimal.valueOf(1.43)), 0);

		Product product = productControllerTestComponent.getProduct(products.get(0).getId());

		assertEquals(product.getAvailableQty().compareTo(BigDecimal.valueOf(12l)), 0);

		assertEquals(product.getOfferedQty().compareTo(BigDecimal.valueOf(10l)), 0);

		assertEquals(product.getOrderedQty().compareTo(BigDecimal.valueOf(0l)), 0);

		assertEquals(product.getPurchasePrice().compareTo(BigDecimal.valueOf(97.08)), 0);


		// Edit the offer

		offers.set(0, offerControllerTestComponent.updateOffer(offers.get(0).getId(), String.format("""
{
	"validUntil":"%s",
	"company":{"id":%d},
	"vat":17.00,
	"notes":"Lead time: 10 days",
	"comments":"",
	"status":"ACTIVE"
}
							""", ZonedDateTime.of(LocalDate.now().plusDays(2), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT), companies.get(1).getId()), "admin"));


		// Try item operations

		List<Item> items = itemControllerTestComponent.getOfferItems(offers.get(0).getId());

		assertEquals(items.get(0), itemControllerTestComponent.getOfferItem(offers.get(0).getId(), items.get(0).getId()));


		// Cancel the offer and duplicate it to make another one

		offerControllerTestComponent.cancelOffer(offers.get(0).getId(), "No reason");

		product = productControllerTestComponent.getProduct(products.get(0).getId());

		assertEquals(product.getAvailableQty().compareTo(BigDecimal.valueOf(22l)), 0);

		assertEquals(product.getOfferedQty().compareTo(BigDecimal.valueOf(0l)), 0);

		assertEquals(product.getOrderedQty().compareTo(BigDecimal.valueOf(0l)), 0);

		assertEquals(product.getPurchasePrice().compareTo(BigDecimal.valueOf(97.27)), 0);

		offers.add(offerControllerTestComponent.duplicateOffer(offers.get(0).getId(), "admin"));

		product = productControllerTestComponent.getProduct(products.get(0).getId());

		assertEquals(product.getAvailableQty().compareTo(BigDecimal.valueOf(12l)), 0);

		assertEquals(product.getOfferedQty().compareTo(BigDecimal.valueOf(10l)), 0);

		assertEquals(product.getOrderedQty().compareTo(BigDecimal.valueOf(0l)), 0);

		assertEquals(product.getPurchasePrice().compareTo(BigDecimal.valueOf(97.08)), 0);


		// Now, accept the offer and test field calculation

		offers.set(1, offerControllerTestComponent.acceptOffer(offers.get(1).getId()));

		product = productControllerTestComponent.getProduct(products.get(0).getId());

		assertEquals(product.getAvailableQty().compareTo(BigDecimal.valueOf(12l)), 0);

		assertEquals(product.getOfferedQty().compareTo(BigDecimal.valueOf(0l)), 0);

		assertEquals(product.getOrderedQty().compareTo(BigDecimal.valueOf(10l)), 0);

		assertEquals(product.getPurchasePrice().compareTo(BigDecimal.valueOf(97.08)), 0);


		// Test deletion failure (accepted offer can not be deleted)

		assertThrows(AssertionError.class, () -> {
			offerControllerTestComponent.deleteOffer(offers.get(1).getId());
		});


		// Add 4 more offers and verify field calculation

		offers.add(offerControllerTestComponent.addOffer(String.format("""
{
	"validUntil":"%s",
	"company":{"id":%d},
	"vat":17.00,
	"notes":"Lead time: 10 days",
	"comments":"",
	"status":"ACTIVE"
}
							""", ZonedDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT), companies.get(1).getId()), "admin"));

		itemBody  = "[" + String.format(itemTemplate, stockInfos.get(2).getId(), products.get(1).getName(), 3.0, products.get(1).getPrice(), 5.00) + ",";
		itemBody += 	  String.format(itemTemplate, stockInfos.get(3).getId(), products.get(1).getName(), 7.0, products.get(1).getPrice(), 5.00) + ",";
		itemBody += 	  String.format(itemTemplate, stockInfos.get(5).getId(), products.get(2).getName(), 5.0, products.get(2).getPrice(), 5.00) + "]";

		itemControllerTestComponent.addItems(offers.get(2).getId(), itemBody);

		offers.set(2, offerControllerTestComponent.getOffer(offers.get(2).getId()));

		assertEquals(offers.get(2).getCost().compareTo(BigDecimal.valueOf(1440.0)), 0);
		assertEquals(offers.get(2).getValue().compareTo(BigDecimal.valueOf(1806.1)), 0);
		assertEquals(offers.get(2).getMargin().compareTo(BigDecimal.valueOf(25.42)), 0);



		offers.add(offerControllerTestComponent.addOffer(String.format("""
{
	"validUntil":"%s",
	"company":{"id":%d},
	"vat":17.00,
	"notes":"Lead time: 10 days",
	"comments":"",
	"status":"ACTIVE"
}
							""", ZonedDateTime.of(LocalDate.now().plusDays(0), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT), companies.get(2).getId()), "admin"));

		itemBody = "[" + String.format(itemTemplate, stockInfos.get(3).getId(), products.get(1).getName(), 2.0, products.get(1).getPrice(), 0.00) + ",";
		itemBody +=      String.format(itemTemplate, stockInfos.get(5).getId(), products.get(2).getName(), 8.0, products.get(2).getPrice(), 0.00) + ",";
		itemBody +=      String.format(itemTemplate, stockInfos.get(6).getId(), products.get(3).getName(), 4.0, products.get(3).getPrice(), 0.00) + "]";

		itemControllerTestComponent.addItems(offers.get(3).getId(), itemBody);


		offers.set(3, offerControllerTestComponent.getOffer(offers.get(3).getId()));

		assertEquals(offers.get(3).getCost().compareTo(BigDecimal.valueOf(1350.0)), 0);
		assertEquals(offers.get(3).getValue().compareTo(BigDecimal.valueOf(1895.34)), 0);
		assertEquals(offers.get(3).getMargin().compareTo(BigDecimal.valueOf(40.40)), 0);



		offers.add(offerControllerTestComponent.addOffer(String.format("""
{
	"validUntil":"%s",
	"company":{"id":%d},
	"vat":17.00,
	"notes":"Lead time: 10 days",
	"comments":"",
	"status":"ACTIVE"
}
							""", ZonedDateTime.of(LocalDate.now().plusDays(7), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT), companies.get(3).getId()), "admin"));

		itemBody = "[" + String.format(itemTemplate, stockInfos.get(4).getId(), products.get(2).getName(),  1.0, products.get(2).getPrice(), 0.00) + ",";
		itemBody +=      String.format(itemTemplate, stockInfos.get(5).getId(), products.get(2).getName(), 12.0, products.get(2).getPrice(), 0.00) + ",";
		itemBody +=      String.format(itemTemplate, stockInfos.get(7).getId(), products.get(3).getName(),  5.0, products.get(3).getPrice(), 0.00) + "]";

		itemControllerTestComponent.addItems(offers.get(4).getId(), itemBody);


		offers.set(4, offerControllerTestComponent.getOffer(offers.get(4).getId()));

		assertEquals(offers.get(4).getCost().compareTo(BigDecimal.valueOf(1715.0)), 0);
		assertEquals(offers.get(4).getValue().compareTo(BigDecimal.valueOf(2460.54)), 0);
		assertEquals(offers.get(4).getMargin().compareTo(BigDecimal.valueOf(43.47)), 0);



		offers.add(offerControllerTestComponent.addOffer(String.format("""
{
	"validUntil":"%s",
	"company":{"id":%d},
	"vat":17.00,
	"notes":"Lead time: 10 days",
	"comments":"",
	"status":"ACTIVE"
}
							""", ZonedDateTime.of(LocalDate.now().plusDays(7), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT), companies.get(4).getId()), "admin"));


		itemBody = "[" + String.format(itemTemplate, stockInfos.get(7).getId(), products.get(3).getName(), 2.0, products.get(3).getPrice(), 0.00) + ",";
		itemBody +=      String.format(itemTemplate, stockInfos.get(8).getId(), products.get(4).getName(), 2.0, products.get(4).getPrice(), 0.00) + "]";

		itemControllerTestComponent.addItems(offers.get(5).getId(), itemBody);


		offers.set(5, offerControllerTestComponent.getOffer(offers.get(5).getId()));

		assertEquals(offers.get(5).getCost().compareTo(BigDecimal.valueOf(390.0)), 0);
		assertEquals(offers.get(5).getValue().compareTo(BigDecimal.valueOf(604.02)), 0);
		assertEquals(offers.get(5).getMargin().compareTo(BigDecimal.valueOf(54.88)), 0);


		/*
		 * Accept two more offers.
		 */

		offerControllerTestComponent.acceptOffer(offers.get(2).getId());

		offerControllerTestComponent.acceptOffer(offers.get(5).getId());

		/*
		 * Now we have one canceled offer (index 0), three accepted offers (indexes: 1, 2, 5)
		 * and two ACTIVE offers (indexes 3 and 4).
		 * Accepted offers span all five warehouses, so we should have 5 orders.
		 */


		// Offer search (try all combinations)

		assertEquals(offerControllerTestComponent.getOffers("none", null, null, null).size(), 0);
		assertEquals(offerControllerTestComponent.getOffers(null, null, null, null).size(), 6);
		assertEquals(offerControllerTestComponent.getOffers("admin", null, null, null).size(), 6);
		assertEquals(offerControllerTestComponent.getOffers(null, companies.get(1).getId(), null, null).size(), 3);
		assertEquals(offerControllerTestComponent.getOffers(null, null, com.ambraspace.etprodaja.model.offer.Offer.Status.ACTIVE, null).size(), 2);
		assertEquals(offerControllerTestComponent.getOffers(null, null, null, products.get(0).getId()).size(), 2);
		assertEquals(offerControllerTestComponent.getOffers("admin", companies.get(1).getId(), null, null).size(), 3);
		assertEquals(offerControllerTestComponent.getOffers("admin", null, com.ambraspace.etprodaja.model.offer.Offer.Status.ACCEPTED, null).size(), 3);
		assertEquals(offerControllerTestComponent.getOffers("admin", null, null, products.get(1).getId()).size(), 2);
		assertEquals(offerControllerTestComponent.getOffers(null, companies.get(1).getId(), com.ambraspace.etprodaja.model.offer.Offer.Status.ACCEPTED, null).size(), 2);
		assertEquals(offerControllerTestComponent.getOffers(null, companies.get(2).getId(), null, products.get(2).getId()).size(), 1);
		assertEquals(offerControllerTestComponent.getOffers(null, null, com.ambraspace.etprodaja.model.offer.Offer.Status.ACCEPTED, products.get(3).getId()).size(), 1);
		assertEquals(offerControllerTestComponent.getOffers("admin", companies.get(1).getId(), com.ambraspace.etprodaja.model.offer.Offer.Status.CANCELED, null).size(), 1);
		assertEquals(offerControllerTestComponent.getOffers("admin", companies.get(1).getId(), null, products.get(2).getId()).size(), 1);
		assertEquals(offerControllerTestComponent.getOffers("admin", null, com.ambraspace.etprodaja.model.offer.Offer.Status.ACTIVE, products.get(2).getId()).size(), 2);
		assertEquals(offerControllerTestComponent.getOffers(null, companies.get(1).getId(), com.ambraspace.etprodaja.model.offer.Offer.Status.ACCEPTED, products.get(1).getId()).size(), 1);
		assertEquals(offerControllerTestComponent.getOffers("admin", companies.get(1).getId(), com.ambraspace.etprodaja.model.offer.Offer.Status.ACCEPTED, products.get(1).getId()).size(), 1);


		List<Order> orders = orderControllerTestComponent.getOrders(null, null, false);

		assertEquals(orders.size(), 5);

		// Close three order (there are 5 items eligible for delivery)

		orders.set(4, orderControllerTestComponent.closeOrder(orders.get(4).getId()));

		orders.set(3, orderControllerTestComponent.closeOrder(orders.get(3).getId()));

		orders.set(0, orderControllerTestComponent.closeOrder(orders.get(0).getId()));

		orders.set(0, orderControllerTestComponent.getOrder(orders.get(0).getId()));


		assertEquals(orders.get(4).getStatus(), Status.CLOSED);

		assertNotEquals(orders.get(4).getClosureTime(), null);

		assertEquals(orders.get(2).getStatus(), Status.OPEN);

		assertEquals(orders.get(2).getClosureTime(), null);

		assertEquals(orders.get(4).getValue().compareTo(BigDecimal.valueOf(500.0)), 0);

		assertEquals(orders.get(3).getValue().compareTo(BigDecimal.valueOf(775.0)), 0);

		assertEquals(orders.get(2).getValue().compareTo(BigDecimal.valueOf(665.0)), 0);

		assertEquals(orders.get(1).getValue().compareTo(BigDecimal.valueOf(475.0)), 0);

		assertEquals(orders.get(0).getValue().compareTo(BigDecimal.valueOf(390.0)), 0);


		// Create deliveries

		List<Delivery> deliveries = new ArrayList<Delivery>();

		List<DeliveryItem> deliveryItems = new ArrayList<>();

		items.clear();

		items.addAll(itemControllerTestComponent.getOrderItems(orders.get(4).getId(), true));
		items.addAll(itemControllerTestComponent.getOrderItems(orders.get(3).getId(), true));
		items.addAll(itemControllerTestComponent.getOrderItems(orders.get(0).getId(), true));

		assertEquals(items.size(), 5);

		deliveries.add(deliveryControllerTestComponent.addDelivery(String.format("""
{
	"supplier":
	{
		"id":%d
	},
	"supplierReference":"%s",
	"deliveryDate":"%s"
}
				""", companies.get(0).getId(), "Delivery 1", ZonedDateTime.of(LocalDate.now().plusDays(10), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT))));


		deliveryItems.addAll(deliveryItemControllerTestComponent.addDeliveryItems(
				deliveries.get(0).getId(), String.format(
				"""
[{
	"quantity":%.2f,
	"deliveryNote":"%s",
	"item":{"id":%d}
}]
				""",
				items.get(1).getQuantity(),
				"Poručena cjelokupna količina",
				items.get(1).getId())));

		deliveryItems.addAll(deliveryItemControllerTestComponent.addDeliveryItems(
				deliveries.get(0).getId(), String.format(
				"""
[{
	"quantity":%.2f,
	"deliveryNote":"%s",
	"item":{"id":%d}
}]
				""",
				items.get(2).getQuantity().subtract(BigDecimal.ONE),
				"Poručen jedan manje",
				items.get(2).getId())));


		assertEquals(itemControllerTestComponent.getOrderItems(orders.get(3).getId(), false).size(), 2);
		assertEquals(itemControllerTestComponent.getOrderItems(orders.get(3).getId(), true).size(), 1);
		assertEquals(itemControllerTestComponent.getOrderItems(orders.get(3).getId(), true)
				.get(0).getOutstandingQuantity().compareTo(BigDecimal.ONE), 0);


		deliveries.add(deliveryControllerTestComponent.addDelivery(String.format("""
{
	"supplier":
	{
		"id":%d
	},
	"supplierReference":"%s",
	"deliveryDate":"%s"
}
				""", companies.get(1).getId(), "Delivery 2", ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT))));


		deliveryItems.addAll(deliveryItemControllerTestComponent.addDeliveryItems(
				deliveries.get(1).getId(), String.format(
				"""
[{
	"quantity":%.2f,
	"deliveryNote":"%s",
	"item":{"id":%d}
}]
				""",
				items.get(0).getQuantity(),
				"",
				items.get(0).getId())));

		deliveryItems.addAll(deliveryItemControllerTestComponent.addDeliveryItems(
				deliveries.get(1).getId(), String.format(
				"""
[{
	"quantity":%.2f,
	"deliveryNote":"%s",
	"item":{"id":%d}
}]
				""",
				items.get(3).getQuantity().subtract(BigDecimal.ONE),
				"",
				items.get(3).getId())));


		deliveries.add(deliveryControllerTestComponent.addDelivery(String.format("""
{
	"supplier":
	{
		"id":%d
	},
	"supplierReference":"%s",
	"deliveryDate":"%s"
}
				""", companies.get(1).getId(), "Delivery 3", ZonedDateTime.of(LocalDate.now().plusDays(-3), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT))));


		deliveryItems.addAll(deliveryItemControllerTestComponent.addDeliveryItems(
				deliveries.get(2).getId(), String.format(
				"""
[{
	"quantity":%.2f,
	"deliveryNote":"%s",
	"item":{"id":%d}
}]
				""",
				items.get(4).getQuantity(),
				"",
				items.get(4).getId())));


		deliveries.set(1, deliveryControllerTestComponent.setDelivered(deliveries.get(1).getId()));

		deliveries.set(2, deliveryControllerTestComponent.updateDelivery(deliveries.get(2).getId(), String.format("""
{
	"id":%d,
	"supplier":
	{
		"id":%d
	},
	"supplierReference":"%s",
	"deliveryDate":"%s"
}
				""", deliveries.get(2).getId(), companies.get(2).getId(), "Delivery 3", ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT))));


		assertEquals(deliveryItemControllerTestComponent.getDeliveryItems(deliveries.get(0).getId()).size(), 2);


		itemControllerTestComponent.updateItems(offers.get(1).getId(), String.format("""
			[{
				"id":%d,
				"stockInfo":{"id":%d},
				"productName":"%s",
				"quantity":%.2f,
				"grossPrice":%.2f,
				"discountPercent":%.2f
			}]
			""",
										items.get(0).getId(),
										items.get(0).getStockInfo().getId(),
										items.get(0).getProductName(),
										items.get(0).getQuantity(),
										items.get(0).getGrossPrice(),
										items.get(0).getDiscountPercent(),
										"Item changed with similar"));

		deliveryControllerTestComponent.setDelivered(deliveries.get(2).getId());

		deliveries.set(2, deliveryControllerTestComponent.getDelivery(deliveries.get(2).getId()));

		assertEquals(deliveries.get(2).getStatus(), com.ambraspace.etprodaja.model.delivery.Delivery.Status.DELIVERED);


		/*
		 *  Now we have full set of objects (offers, orders, deliveries).
		 *  Let's make final tests.
		 */

		// Load fresh products from the database
		for (int i = 0; i < products.size(); i++)
		{
			products.set(i, productControllerTestComponent.getProduct(products.get(i).getId()));
		}


		// Test transient fields calculation

		assertEquals(products.get(0).getAvailableQty().compareTo(BigDecimal.valueOf(12)), 0);
		assertEquals(products.get(0).getOfferedQty().compareTo(BigDecimal.valueOf(0)), 0);
		assertEquals(products.get(0).getOrderedQty().compareTo(BigDecimal.valueOf(10)), 0);
		assertEquals(products.get(0).getPurchasePrice().compareTo(BigDecimal.valueOf(97.08)), 0);

		assertEquals(products.get(1).getAvailableQty().compareTo(BigDecimal.valueOf(10)), 0);
		assertEquals(products.get(1).getOfferedQty().compareTo(BigDecimal.valueOf(2)), 0);
		assertEquals(products.get(1).getOrderedQty().compareTo(BigDecimal.valueOf(10)), 0);
		assertEquals(products.get(1).getPurchasePrice().compareTo(BigDecimal.valueOf(98.5)), 0);

		assertEquals(products.get(2).getAvailableQty().compareTo(BigDecimal.valueOf(-4)), 0);
		assertEquals(products.get(2).getOfferedQty().compareTo(BigDecimal.valueOf(21)), 0);
		assertEquals(products.get(2).getOrderedQty().compareTo(BigDecimal.valueOf(5)), 0);
		assertEquals(products.get(2).getPurchasePrice().compareTo(BigDecimal.valueOf(83.75)), 0);

		assertEquals(products.get(3).getAvailableQty().compareTo(BigDecimal.valueOf(11)), 0);
		assertEquals(products.get(3).getOfferedQty().compareTo(BigDecimal.valueOf(9)), 0);
		assertEquals(products.get(3).getOrderedQty().compareTo(BigDecimal.valueOf(2)), 0);
		assertEquals(products.get(3).getPurchasePrice().compareTo(BigDecimal.valueOf(97.73)), 0);

		assertEquals(products.get(4).getAvailableQty().compareTo(BigDecimal.valueOf(20)), 0);
		assertEquals(products.get(4).getOfferedQty().compareTo(BigDecimal.valueOf(0)), 0);
		assertEquals(products.get(4).getOrderedQty().compareTo(BigDecimal.valueOf(2)), 0);
		assertEquals(products.get(4).getPurchasePrice().compareTo(BigDecimal.valueOf(97)), 0);

		// Test transient fields calculation again with focus on single warehouse

		List<Product> productsPerWarehouse = productControllerTestComponent.getProducts(null, null, warehouses.get(1).getId(), null, null);

		assertEquals(productsPerWarehouse.get(0).getAvailableQty().compareTo(BigDecimal.valueOf(7)), 0);
		assertEquals(productsPerWarehouse.get(0).getOfferedQty().compareTo(BigDecimal.valueOf(0)), 0);
		assertEquals(productsPerWarehouse.get(0).getOrderedQty().compareTo(BigDecimal.valueOf(5)), 0);
		assertEquals(productsPerWarehouse.get(0).getPurchasePrice().compareTo(BigDecimal.valueOf(95.00)), 0);

		assertEquals(productsPerWarehouse.get(1).getAvailableQty().compareTo(BigDecimal.valueOf(7)), 0);
		assertEquals(productsPerWarehouse.get(1).getOfferedQty().compareTo(BigDecimal.valueOf(0)), 0);
		assertEquals(productsPerWarehouse.get(1).getOrderedQty().compareTo(BigDecimal.valueOf(3)), 0);
		assertEquals(productsPerWarehouse.get(1).getPurchasePrice().compareTo(BigDecimal.valueOf(100)), 0);


		// Order search

		assertEquals(orderControllerTestComponent.getOrders(null, null, false).size(), 5);
		assertEquals(orderControllerTestComponent.getOrders(warehouses.get(0).getId(), null, false).size(), 1);
		assertEquals(orderControllerTestComponent.getOrders(null, Status.OPEN, false).size(), 2);
		assertEquals(orderControllerTestComponent.getOrders(null, null, true).size(), 4);
		assertEquals(orderControllerTestComponent.getOrders(warehouses.get(0).getId(), Status.CLOSED, false).size(), 1);
		assertEquals(orderControllerTestComponent.getOrders(warehouses.get(1).getId(), null, true).size(), 1);
		assertEquals(orderControllerTestComponent.getOrders(null, Status.CLOSED, true).size(), 2);
		assertEquals(orderControllerTestComponent.getOrders(warehouses.get(1).getId(), Status.CLOSED, true).size(), 1);


		// Delivery search

		assertEquals(deliveryControllerTestComponent.getDeliveries(null, null).size(), 3);
		assertEquals(deliveryControllerTestComponent.getDeliveries(companies.get(0).getId(), null).size(), 1);
		assertEquals(deliveryControllerTestComponent.getDeliveries(null, com.ambraspace.etprodaja.model.delivery.Delivery.Status.DELIVERED).size(), 2);
		assertEquals(deliveryControllerTestComponent.getDeliveries(companies.get(1).getId(), com.ambraspace.etprodaja.model.delivery.Delivery.Status.DELIVERED).size(), 1);


		deliveryItems.set(0, deliveryItemControllerTestComponent.updateDeliveryItem(
				deliveries.get(0).getId(), deliveryItems.get(0).getId(), String.format(
				"""
{
	"quantity":%.2f,
	"deliveryNote":"%s",
	"item":{"id":%d}
}
				""",
				deliveryItems.get(0).getQuantity().subtract(BigDecimal.ONE),
				"Umanjeno za 1",
				deliveryItems.get(0).getItem().getId())));

		assertEquals(deliveryItems.get(0).getQuantity(), items.get(1).getQuantity().subtract(BigDecimal.ONE));
		assertEquals(deliveryItems.get(0).getDeliveryNote(), "Umanjeno za 1");

		deliveryItems.set(1, deliveryItemControllerTestComponent.getDeliveryItem(
				deliveryItems.get(1).getDelivery().getId(), deliveryItems.get(1).getId()));

		deliveryItemControllerTestComponent.deleteDeliveryItem(
				deliveries.get(0).getId(), deliveryItems.get(0).getId());

		// Delete tested objects

		itemControllerTestComponent.deleteItem(offers.get(1).getId(), items.get(0).getId());

		assertEquals(itemControllerTestComponent.getOfferItem(offers.get(1).getId(), items.get(0).getId()), null);

		deliveryControllerTestComponent.deleteDelivery(deliveries.get(2).getId());

		deliveryControllerTestComponent.deleteAllDeliveries();

		orderControllerTestComponent.deleteAllOrders();

		offerControllerTestComponent.deleteOffer(offers.get(0).getId());

		offerControllerTestComponent.deleteAllOffers();

		// Cleanup

		deleteStockInfos(stockInfos);

		deleteProducts(products);

		previewService.deleteOrphanPreviews();

		deleteCategories(categories);

		deleteWarehouses(warehouses);

		deleteCompanies(companies);


	}


	private void addCompanies(List<Company> companies) throws Exception
	{

		String companyTemplate = """
{
	"name":"%s",
	"locality":"%s"
}
				""";

		companies.add(companyControllerTestComponent
				.addCompany(String.format(companyTemplate, "Test company 1", "Locality 1")));

		companies.add(companyControllerTestComponent
				.addCompany(String.format(companyTemplate, "Test company 2", "Locality 2")));

		companies.add(companyControllerTestComponent
				.addCompany(String.format(companyTemplate, "Test company 3", "Locality 3")));

		companies.add(companyControllerTestComponent
				.addCompany(String.format(companyTemplate, "Test company 4", "Locality 4")));

		companies.add(companyControllerTestComponent
				.addCompany(String.format(companyTemplate, "Test company 5", "Locality 5")));

	}


	private void addWarehouses(List<Company> companies, List<Warehouse> warehouses) throws Exception
	{

		String warehouseTemplate = """
{
	"name":"%s"
}
				""";

		for (int i = 0; i < companies.size(); i++)
		{
			warehouses.add(warehouseControllerTestComponent
					.addWarehouse(companies.get(i).getId(), String.format(warehouseTemplate, "Warehouse " + i)));
		}

	}


	private void addCategories(List<Category> categories) throws Exception
	{

		categories.addAll(categoryControllerTestComponent.saveCategories("""
[
	{
		"name":"Test category 1",
		"children":[]
	},
	{
		"name":"Test category 2",
		"children":[]
	},
	{
		"name":"Test category 3",
		"children":[]
	},
	{
		"name":"Test category 4",
		"children":[]
	},
	{
		"name":"Test category 5",
		"children":[]
	}
]
				"""));
	}


	private void addProducts(List<Category> categories, List<Product> products) throws Exception
	{

		List<Preview> previews = previewControllerTestComponent.addPreviews(15);

		String productTemplate = """
{
	"name":"%s",
	"unit":"pcs.",
	"price":%.2f,
	"category":
		{
			"id":%d
		},
	"tags":
		[
		],
	"previews":%s,
	"comment":"This is a test"
}
				""";

		products.add(productControllerTestComponent
				.addProduct(String.format(productTemplate, "Product 1", 111.11, categories.get(0).getId(), makePreviewsObject(previews.subList(0, 3)))));

		products.add(productControllerTestComponent
				.addProduct(String.format(productTemplate, "Product 2", 123.45, categories.get(1).getId(), makePreviewsObject(previews.subList(3, 6)))));

		products.add(productControllerTestComponent
				.addProduct(String.format(productTemplate, "Product 3", 133.33, categories.get(2).getId(), makePreviewsObject(previews.subList(6, 9)))));

		products.add(productControllerTestComponent
				.addProduct(String.format(productTemplate, "Product 4", 145.45, categories.get(3).getId(), makePreviewsObject(previews.subList(9, 12)))));

		products.add(productControllerTestComponent
				.addProduct(String.format(productTemplate, "Product 5", 156.56, categories.get(4).getId(), makePreviewsObject(previews.subList(12, 15)))));

	}


	private String makePreviewsObject(List<Preview> previews)
	{

		StringBuilder retVal = new StringBuilder();
		retVal.append("[");
		for (int i = 0; i < previews.size(); i++)
		{
			Preview pr = previews.get(i);
			retVal.append(String.format("""
{
		"id":%d,
		"fileName":"%s",
		"originalFileName":"%s",
		"size":%d,
		"primary":false
}
					""", pr.getId(), pr.getFileName(), pr.getOriginalFileName(), pr.getSize()));
			if (i < previews.size() - 1) retVal.append(",");
		}
		retVal.append("]");

		return retVal.toString();

	}


	private void addStockInfos(List<Product> products, List<Warehouse> warehouses, List<StockInfo> stockInfos) throws Exception
	{

		String stockInfoTemplate = """
{
	"warehouse":
	{
		"id":%d
	},
	"customerReference":"%s",
	"quantity":%.2f,
	"unitPrice":%.2f
}
				""";

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(0).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(0).getId(), "PR001",	10.0,	100.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(0).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(1).getId(), "PR001",	12.0,	95.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(1).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(1).getId(), "PR002",	10.0,	100.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(1).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(2).getId(), "PR002",	12.0,	95.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(2).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(2).getId(), "PR003",	10.0,	100.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(2).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(3).getId(), "PR003",	12.0,	95.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(3).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(3).getId(), "PR004",	10.0,	100.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(3).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(4).getId(), "PR004",	12.0,	95.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(4).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(4).getId(), "PR005",	10.0,	100.0)));

		stockInfos.add(stockInfoControllerTestComponent
				.addStockInfo(
						products.get(4).getId(),
						String.format(stockInfoTemplate,
								warehouses.get(0).getId(), "PR005",	12.0,	95.0)));

	}


	private void deleteCompanies(List<Company> companies) throws Exception
	{

		for (int i = 0; i < companies.size(); i++)
		{
			companyControllerTestComponent.deleteCompany(companies.get(i).getId());
		}

	}


	private void deleteWarehouses(List<Warehouse> warehouses) throws Exception
	{

		for (int i = 0; i < warehouses.size(); i++)
		{
			warehouseControllerTestComponent
				.deleteWarehouse(warehouses.get(i).getCompany().getId(), warehouses.get(i).getId());
		}

	}


	private void deleteCategories(List<Category> categories) throws Exception
	{

		categories = categoryControllerTestComponent.saveCategories("[]");

	}


	private void deleteProducts(List<Product> products) throws Exception
	{

		for (int i = 0; i < products.size(); i++)
		{
			productControllerTestComponent.deleteProduct(products.get(i).getId());
		}

	}


	private void deleteStockInfos(List<StockInfo> stockInfos) throws Exception
	{

		for (int i = 0; i < stockInfos.size(); i++)
		{
			stockInfoControllerTestComponent
				.deleteStockInfo(stockInfos.get(i).getProduct().getId(), stockInfos.get(i).getId());
		}

	}

}
