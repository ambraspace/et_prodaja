package com.ambraspace.etprodaja.model.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.product.ProductRepository;

@Service
public class CategoryService
{

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;


	public List<Category> getCategories()
	{
		List<Category> retVal = new ArrayList<Category>();
		categoryRepository.findByRootCategoryIsTrue().forEach(retVal::add);

		sortCategories(retVal);

		return retVal;
	}


	private void sortCategories(List<Category> categories)
	{

		categories.sort(CategoryComparator.getInstance());

		for (Category c:categories)
		{
			if (c.getChildren().size() > 0)
			{
				sortCategories(c.getChildren());
			}
		}

	}


	@Transactional
	public List<Category> saveCategories(List<Category> categories)
	{

		// Set order of categories
		int nextVal = 1;

		for (Category c:categories)
		{
			c.setRootCategory(true);
			nextVal = setOrderOfCategories(c, nextVal);
		}

		// First detach all products assigned to categories which are going to be deleted
		List<Category> fromRep = getCategories();

		// Flatten lists of categories
		Set<Category> flatFromRep = new HashSet<Category>();
		Set<Category> flatNew = new HashSet<Category>();

		fromRep.forEach(c -> flatFromRep.addAll(makeItFlat(c)));
		categories.forEach(c -> flatNew.addAll(makeItFlat(c)));

		flatFromRep.removeAll(flatNew);

		// Now we have a set of all categories to be deleted

		if (flatFromRep.size() > 0)
		{

			List<Product> products = new ArrayList<Product>();
			productRepository.findByCategoryIn(flatFromRep).forEach(products::add);

			if (products.size() > 0)
			{

				Category uncategorized = new Category();
				uncategorized.setName("UNCATEGORIZED");
				uncategorized.setRootCategory(true);
				uncategorized.setOrder(nextVal);
				Category saved = categoryRepository.save(uncategorized); // Here we saved the category and assigned an ID to it
				categories.add(saved);

				products.forEach(p -> p.setCategory(uncategorized));

				productRepository.saveAll(products);

				// Products are now detached and earlier categories can be deleted

			}

		}

		// Remove all categories that have been left out in the supplied list of categories
		fromRep.removeAll(categories);

		categoryRepository.deleteAll(fromRep);

		// Now add all supplied categories to the collection
		fromRep.clear();

		fromRep.addAll(categories);

		categoryRepository.saveAll(fromRep);

		return fromRep;

	}


	private int setOrderOfCategories(Category category, int nextVal)
	{

		category.setOrder(nextVal);

		nextVal++;

		if (category.getChildren() != null && category.getChildren().size() > 0)
		{
			for (Category c : category.getChildren())
			{
				nextVal = setOrderOfCategories(c, nextVal);
			}
		}

		return nextVal;

	}


	private Category getCategory(Long id)
	{
		return categoryRepository.findById(id).orElse(null);
	}


	/**
	 * @param categoryId - requested category ID
	 * @return a list containing ID of this category and IDs of all down stream categories
	 */
	public List<Long> collectCategoryIds(Long categoryId)
	{
		List<Long> ids = new ArrayList<Long>();
		Category parent = getCategory(categoryId);
		if (parent != null)
		{
			ids.addAll(collectCategoryIds(parent));
		} else {
			// prevents sending empty list to repository method
			ids.add(-1l);
		}
		return ids;
	}


	private List<Long> collectCategoryIds(Category root)
	{
		List<Long> ids = new ArrayList<Long>();
		ids.add(root.getId());
		for (Category c:root.getChildren())
		{
			ids.addAll(collectCategoryIds(c));
		}
		return ids;
	}


	private List<Category> makeItFlat(Category category)
	{
		List<Category> retVal = new ArrayList<Category>();
		retVal.add(category);
		if (category.getChildren().size() > 0)
		{
			category.getChildren().forEach(c -> retVal.addAll(makeItFlat(c)));
		}
		//category.setChildren(List.of());
		return retVal;
	}

}
