package com.ambraspace.etprodaja.model.tag;

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
import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.product.ProductControllerTestComponent;

@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerTests {

	@Autowired
	private SecurityTestComponent securityTestComponent;

	@Autowired
	private TagControllerTestComponent tagTestComponent;

	@Autowired
	private CategoryControllerTestComponent categoryControllerTestComponent;

	@Autowired
	private ProductControllerTestComponent productControllerTestComponent;


	@Test
	public void testTagOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		Tag tag = tagTestComponent.addTag("""
{
	"name":"Tag 1",
	"color":"#121212"
}
				""");

		assertNotEquals(tag, null);

		tag = tagTestComponent.getTag(tag.getId());

		assertNotEquals(tag, null);

		assertNotEquals(tagTestComponent.getTags().size(), 0);

		List<Tag> tags = tagTestComponent.searchTags("ta");

		assertNotEquals(tags.size(), 0);

		List<Category> categories = categoryControllerTestComponent.saveCategories("""
[
	{
		"name":"Test category",
		"children":[]
	}
]
				""");

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
				"id":%d
			}
		],
	"comment":"This is a test"
}
				""", categories.get(0).getId(), tag.getId());

		Product product = productControllerTestComponent.addProduct(productBody, 3);

		product = productControllerTestComponent.getProduct(product.getId());

		assertEquals(product.getTags().get(0).getId(), tag.getId());

		tagTestComponent.deleteTag(tag.getId());

		tag = tagTestComponent.getTag(tag.getId());

		assertEquals(tag, null);

		product = productControllerTestComponent.getProduct(product.getId());

		assertEquals(product.getTags().size(), 0);

		productControllerTestComponent.deleteProduct(product.getId());

		categoryControllerTestComponent.saveCategories("[]");

	}

}
