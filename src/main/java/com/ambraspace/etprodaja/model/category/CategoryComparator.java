package com.ambraspace.etprodaja.model.category;

import java.util.Comparator;

public class CategoryComparator implements Comparator<Category>
{

	private static final CategoryComparator singleton = new CategoryComparator();


	private CategoryComparator() {}


	public static CategoryComparator getInstance()
	{
		return singleton;
	}


	@Override
	public int compare(Category o1, Category o2)
	{
		return o1.getOrder().compareTo(o2.getOrder());
	}


}
