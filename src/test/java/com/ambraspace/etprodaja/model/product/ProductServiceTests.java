package com.ambraspace.etprodaja.model.product;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTests
{

	@Autowired
	private ProductTestRepository productTestRepository;

	@Autowired
	private ProductService productService;


	@Test
	public void testProductOperations() throws IllegalStateException, IOException
	{

//		List<Category> categories = new ArrayList<Category>();
//
//		Category c = new Category();
//		c.setId(1);
//		c.setParentId(null);
//		c.setName("Kategorija 1");
//		categories.add(c);
//
//		c = new Category();
//		c.setId(2);
//		c.setParentId(1);
//		c.setName("Kategorija 1.1");
//		categories.add(c);
//
//		c = new Category();
//		c.setId(3);
//		c.setParentId(2);
//		c.setName("Kategorija 1.1.1");
//		categories.add(c);
//
//		c = new Category();
//		c.setId(4);
//		c.setParentId(null);
//		c.setName("Kategorija 2");
//		categories.add(c);
//
//		c = new Category();
//		c.setId(5);
//		c.setParentId(4);
//		c.setName("Kategorija 2.1");
//		categories.add(c);
//
//		c = new Category();
//		c.setId(6);
//		c.setParentId(null);
//		c.setName("Kategorija 3");
//		categories.add(c);
//
//		categories = saveCategories(categories);
//
//		List<Product> products = new ArrayList<Product>();
//
//		for (int i = 0; i < 10; i++)
//		{
//			Product p = new Product();
//			p.setCategory(categories.get(i % 6));
//			p.setName("Proizvod " + i);
//			p.setPrice(BigDecimal.valueOf((i + 1) * 100));
//			p.setUnit("kom.");
//			products.add(addProduct(p));
//		}
//
//		List<Tuple> data = productTestRepository.getTestData();
//
//		for (Tuple o:data)
//		{
//			System.out.println("" + o.get(0, Long.class));
//			System.out.println("" + o.get(1, BigDecimal.class));
//			System.out.println("" + o.get(2, BigDecimal.class));
//		}
//
//		products.forEach(p -> deleteProduct(p));
//
//		saveCategories(List.of());

	}


//	private List<Category> saveCategories(List<Category> cat)
//	{
//
//		return productService.saveCategories(cat);
//
//	}

	private Product addProduct(Product p) throws IllegalStateException, IOException
	{

		return productService.addProduct(p, null);

	}


	private void deleteProduct(Product p)
	{
		productService.deleteProduct(p.getId());
	}

}
