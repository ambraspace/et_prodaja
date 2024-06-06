package com.ambraspace.etprodaja.model.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.product.ProductControllerTestComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTests {

	@Autowired
	private SecurityTestComponent securityTestComponent;

	@Autowired
	private CategoryControllerTestComponent categoryControllerTestComponent;

	@Autowired
	private ProductControllerTestComponent productControllerTestComponent;


	public static final ObjectMapper mapper = new ObjectMapper();


	@Test
	public void testCategoryOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		List<Category> categories = categoryControllerTestComponent.saveCategories("""
[
	{
		"name":"Kat 1",
		"children":
			[
				{
					"name":"Kat 1.1",
					"children":
					[
						{
							"name":"Kat 1.1.1",
							"children":[]
						},
						{
							"name":"Kat 1.1.2",
							"children":[]
						}
					]
				},
				{
					"name":"Kat 1.2",
					"children":[]
				}
			]
	},
	{
		"name":"Kat 2",
		"children":
			[
				{
					"name":"Kat 2.1",
					"children":[]
				},
				{
					"name":"Kat 2.2",
					"children":[]
				}
			]
	},
	{
		"name":"Kat 3",
		"children":
			[
			]
	}
]
				""");

		assertEquals(categories.size(), 3);

		assertEquals(categories.get(0).getChildren().size(), 2);

		assertEquals(categories.get(1).getChildren().size(), 2);

		assertEquals(categories.get(0).getChildren().get(0).getChildren().size(), 2);

		categories = categoryControllerTestComponent.getCategories();

		assertEquals(categories.size(), 3);

		Long thirdId = categories.get(2).getId();

		categories = categoryControllerTestComponent.saveCategories(String.format("""
[
	{
	    "id":%d,
		"name":"Kat 1",
		"children":
			[
				{
				    "id":%d,
					"name":"Kat 1.1",
					"children":
					[
						{
						    "id":%d,
							"name":"Kat 1.1.2",
							"children":[]
						}
					]
				},
				{
				    "id":%d,
					"name":"Kat 1.2",
					"children":[]
				}
			]
	},
	{
	    "id":%d,
		"name":"Kat 2",
		"children":
			[
				{
				    "id":%d,
					"name":"Kat 2.1",
					"children":[]
				}
			]
	},
	{
		"name":"Kat 4",
		"children":
			[
				{
					"name":"Kat 4.1",
					"children":
						[
						]
				}
			]
	}
]
				""",
				categories.get(0).getId(),
				categories.get(0).getChildren().get(0).getId(),
				categories.get(0).getChildren().get(0).getChildren().get(1).getId(),
				categories.get(0).getChildren().get(1).getId(),
				categories.get(1).getId(),
				categories.get(1).getChildren().get(0).getId()));

		assertEquals(categories.size(), 3);

		assertEquals(categories.get(0).getChildren().size(), 2);

		assertEquals(categories.get(1).getChildren().size(), 1);

		assertEquals(categories.get(2).getChildren().size(), 1);

		assertEquals(categories.get(0).getChildren().get(0).getChildren().size(), 1);

		assertNotEquals(categories.get(2).getId(), thirdId);

		categories = categoryControllerTestComponent.saveCategories("""
[
	{
		"name":"To be deleted",
		"children":[]
	}
]
				""");

		assertEquals(categories.size(), 1);

		Product product = productControllerTestComponent.addProduct(String.format("""
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
				""", categories.get(0).getId()));

		categories = categoryControllerTestComponent.saveCategories("[]");

		assertEquals(categories.size(), 1);

		assertEquals(categories.get(0).getName(), "UNCATEGORIZED");

		productControllerTestComponent.deleteProduct(product.getId());

		categories = categoryControllerTestComponent.saveCategories("[]");

		assertEquals(categories.size(), 0);

	}

}
