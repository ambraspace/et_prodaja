package com.ambraspace.etprodaja.model.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class CategoryService
{

	@Autowired
	private CategoryRepository categoryRepository;


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


	public List<Category> saveCategories(List<Category> categories)
	{

		List<Category> fromRep = getCategories();

		fromRep.removeAll(categories);

		categoryRepository.deleteAll(fromRep);

		fromRep.clear();

		fromRep.addAll(categories);

		int nextVal = 1;

		for (Category c:fromRep)
		{
			c.setRootCategory(true);
			nextVal = setOrderOfCategories(c, nextVal);
		}

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


	public Category getCategory(Long id)
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

}
