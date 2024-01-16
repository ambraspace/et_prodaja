package com.ambraspace.etprodaja.model.tag;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ambraspace.etprodaja.model.product.ProductService;

@Service
public class TagService
{

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private ProductService productService;


	public Page<Tag> getTags(Pageable pageable)
	{
		return tagRepository.findAll(pageable);
	}


	public Tag getTag(Long id)
	{
		return tagRepository.findById(id).orElse(null);
	}


	public Tag addTag(Tag tag)
	{
		return tagRepository.save(tag);
	}


	public void deleteTag(Long id)
	{

		Tag fromRep = getTag(id);

		if (fromRep == null)
			throw new RuntimeException("No such tag!");

		productService.removeTagFromProducts(id);

		tagRepository.deleteById(id);

	}


	public List<Tag> serachTags(String query)
	{

		List<Tag> retVal = new ArrayList<Tag>();

		tagRepository.findFirst10ByNameLikeIgnoreCase("%" + query + "%").forEach(retVal::add);

		return retVal;

	}



}
