package com.ambraspace.etprodaja.model.tag;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


	public Tag getTag(String id)
	{
		return tagRepository.findById(id).orElse(null);
	}


	public Tag addTag(Tag tag)
	{
		Tag fromRep = getTag(tag.getName());
		if (fromRep == null)
		{
			fromRep = tagRepository.save(tag);
		}
		return fromRep;
	}


	@Transactional
	public void deleteTag(String id)
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


	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deleteOrphanTags()
	{

		List<Tag> allTags = new ArrayList<Tag>();
		tagRepository.findAll().forEach(allTags::add);

		List<Tag> assignedTags = new ArrayList<Tag>();
		tagRepository.getAssignedTags().forEach(assignedTags::add);

		allTags.removeAll(assignedTags);

		tagRepository.deleteAll(allTags);

	}



}
