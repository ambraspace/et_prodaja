package com.ambraspace.etprodaja.model.product;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ambraspace.etprodaja.model.category.CategoryService;
import com.ambraspace.etprodaja.model.offer.OfferService;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;
import com.ambraspace.etprodaja.model.stockinfo.StockInfoService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Tuple;

@Service @Transactional
public class ProductService
{

	@Value("${et-prodaja.storage-location}")
	private String storageLocation;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private StockInfoService stockInfoService;

	@Autowired
	private OfferService offerService;


	public Page<Product> getProducts(String query, Boolean includeComments, Long warehouseId, List<Long> tagIds, Long categoryId, Pageable pageable)
	{

		Page<Product> retVal;

		if (warehouseId != null && warehouseId > 0)
		{

			if (query == null || query.trim().equals(""))
			{
				if (tagIds != null && tagIds.size() > 0)
				{
					if (categoryId != null)
					{
						//Search by warehouse, tags and category
						retVal = productRepository.findByWarehouseTagsAndCategory(warehouseId, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						//Search by warehouse and tags
						retVal = productRepository.findByWarehouseAndTags(warehouseId, tagIds, pageable);
					}
				} else {
					if (categoryId != null)
					{
						// Search by warehouse and category
						retVal = productRepository.findByWarehouseAndCategory(warehouseId, categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						// Search by warehouse
						retVal = productRepository.findByWarehouse(warehouseId, pageable);
					}
				}
			} else {
				if (includeComments)
				{
					if (tagIds != null && tagIds.size() > 0)
					{
						if (categoryId != null)
						{
							//Search by warehouse, name, comment, tags and category
							retVal = productRepository.findByWarehouseNameCommentTagsAndCategory(warehouseId, query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse, name, comment and tags
							retVal = productRepository.findByWarehouseNameCommentAndTags(warehouseId, query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by warehouse, name, comment and category
							retVal = productRepository.findByWarehouseNameCommentAndCategory(warehouseId, query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse, name and comment
							retVal = productRepository.findByWarehouseNameAndComment(warehouseId, query, pageable);
						}
					}
				} else {
					if (tagIds != null && tagIds.size() > 0)
					{
						if (categoryId != null)
						{
							//Search by warehouse, name, tags and category
							retVal = productRepository.findByWarehouseNameTagsAndCategory(warehouseId, query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse, name and tags
							retVal = productRepository.findByWarehouseNameAndTags(warehouseId, query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by warehouse, name and category
							retVal = productRepository.findByWarehouseNameAndCategory(warehouseId, query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse and name
							retVal = productRepository.findByWarehouseAndName(warehouseId, query, pageable);
						}
					}
				}
			}
		} else {
			if (query == null || query.trim().equals(""))
			{
				if (tagIds != null && tagIds.size() > 0)
				{
					if (categoryId != null)
					{
						//Search by tags and category
						retVal = productRepository.findByTagsAndCategory(tagIds, categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						//Search by tags
						retVal = productRepository.findByTags(tagIds, pageable);
					}
				} else {
					if (categoryId != null)
					{
						// Search by category
						retVal = productRepository.findByCategory_IdIsIn(categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						// Search all
						retVal = productRepository.findAll(pageable);
					}
				}
			} else {
				if (includeComments)
				{
					if (tagIds != null && tagIds.size() > 0)
					{
						if (categoryId != null)
						{
							//Search by name, comment, tags and category
							retVal = productRepository.findByNameCommentTagsAndCategory(query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name, comment and tags
							retVal = productRepository.findByNameCommentAndTags(query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by name, comment and category
							retVal = productRepository.findByNameCommentAndCategory(query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name and comment
							retVal = productRepository.findByNameAndComment(query, pageable);
						}
					}
				} else {
					if (tagIds != null && tagIds.size() > 0)
					{
						if (categoryId != null)
						{
							//Search by name, tags and category
							retVal = productRepository.findByNameTagsAndCategory(query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name and tags
							retVal = productRepository.findByNameAndTags(query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by name and category
							retVal = productRepository.findByNameAndCategory(query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name
							retVal = productRepository.findByName(query, pageable);
						}
					}
				}
			}
		}

		if (retVal.hasContent())
		{

			fillTransientFields(warehouseId, retVal.getContent());

		}

		return retVal;

	}


	public Product getProduct(Long id)
	{
		Product p = productRepository.findById(id).orElse(null);
		if (p != null)
		{
			fillTransientFields(null, List.of(p));
		}
		return p;
	}


	/*
	 * TODO: Test thoroughly (even when available qty is negative)
	 * Test case #1: Product with no StockInfo data and no Item data
	 * Test case #2: Product with no StockInfo data but with Item data for Offers
	 * Test case #3: Product with no StockInfo data but with Item data for Offers and Orders
	 * Test case #4: Product with StockInfo data and no Item data
	 * Test case #5: Product with StockInfo data and Item data for Offers
	 * Test case #6: Product with StockInfo data and Item data for Offers and Orders
	 * Each test should include multiple Warehouses with different quantities and unit prices
	 */
	private void fillTransientFields(Long warehouseId, List<Product> products)
	{

		if (warehouseId != null && warehouseId > 0)
		{

			List<StockInfo> stockInfos = stockInfoService.getStockInfoByWarehouseIdAndProducts(warehouseId, products);
			List<Tuple> itemDataForOffers = offerService.getItemDataForProductsInValidOffersByWarehouse(warehouseId, products);
			List<Tuple> itemDataForOrders = offerService.getItemDataForOrderedProductsByWarehouse(warehouseId, products);

			products.forEach(p -> {

				StockInfo si = stockInfos.stream().filter(s -> p.getId().equals(s.getProduct().getId())).findFirst().orElse(null);
				Tuple dataForOffers = itemDataForOffers.stream().filter(o -> p.getId().equals(o.get(0, Long.class))).findFirst().orElse(null);
				Tuple dataForOrders = itemDataForOrders.stream().filter(o -> p.getId().equals(o.get(0, Long.class))).findFirst().orElse(null);

				BigDecimal offeredQty = dataForOffers != null ? dataForOffers.get(1, BigDecimal.class) : BigDecimal.ZERO;
				BigDecimal orderedQty = dataForOrders != null ? dataForOrders.get(1, BigDecimal.class) : BigDecimal.ZERO;

				BigDecimal availableQty = (si != null ? si.getQuantity() : BigDecimal.ZERO)
						.subtract(offeredQty)
						.subtract(orderedQty);

				BigDecimal offeredValue = dataForOffers != null ? dataForOffers.get(2, BigDecimal.class) : BigDecimal.ZERO;
				BigDecimal orderedValue = dataForOrders != null ? dataForOrders.get(2, BigDecimal.class) : BigDecimal.ZERO;

				BigDecimal availableValue =
						(si != null ? si.getQuantity().multiply(si.getUnitPrice()).setScale(2) : BigDecimal.ZERO)
						.subtract(offeredValue)
						.subtract(orderedValue);

				p.setAvailableQty(availableQty);
				p.setOfferedQty(offeredQty);
				p.setOrderedQty(orderedQty);
				p.setPurchasePrice(availableQty.compareTo(
						BigDecimal.ZERO) == 0 ?
								BigDecimal.ZERO
								: availableValue.divide(availableQty, 2, RoundingMode.HALF_EVEN));

			});

		} else {

			List<Tuple> stockInfos = stockInfoService.getStockInfoByProducts(products);
			List<Tuple> itemDataForOffers = offerService.getItemDataForProductsInValidOffers(products);
			List<Tuple> itemDataForOrders = offerService.getItemDataForOrderedProducts(products);

			products.forEach(p -> {

				Tuple si = stockInfos.stream().filter(s -> p.getId().equals(s.get(0, Long.class))).findFirst().orElse(null);
				Tuple dataForOffers = itemDataForOffers.stream().filter(o -> p.getId().equals(o.get(0, Long.class))).findFirst().orElse(null);
				Tuple dataForOrders = itemDataForOrders.stream().filter(o -> p.getId().equals(o.get(0, Long.class))).findFirst().orElse(null);

				BigDecimal offeredQty = dataForOffers != null ? dataForOffers.get(1, BigDecimal.class) : BigDecimal.ZERO;
				BigDecimal orderedQty = dataForOrders != null ? dataForOrders.get(1, BigDecimal.class) : BigDecimal.ZERO;

				BigDecimal availableQty = (si != null ? si.get(1, BigDecimal.class) : BigDecimal.ZERO)
						.subtract(offeredQty)
						.subtract(orderedQty);

				BigDecimal offeredValue = dataForOffers != null ? dataForOffers.get(2, BigDecimal.class) : BigDecimal.ZERO;
				BigDecimal orderedValue = dataForOrders != null ? dataForOrders.get(2, BigDecimal.class) : BigDecimal.ZERO;

				BigDecimal availableValue =
						(si != null ? si.get(2, BigDecimal.class) : BigDecimal.ZERO)
						.subtract(offeredValue)
						.subtract(orderedValue);

				p.setAvailableQty(availableQty);
				p.setOfferedQty(offeredQty);
				p.setOrderedQty(orderedQty);
				p.setPurchasePrice(
						availableQty.compareTo(BigDecimal.ZERO) == 0 ?
								BigDecimal.ZERO
								: availableValue.divide(availableQty, 2, RoundingMode.HALF_EVEN));

			});

		}

	}



	public Product addProduct(Product product, List<MultipartFile> files) throws IllegalStateException, IOException
	{

		product.getPreviews().clear();

		if (files != null && files.size() > 0)
		{
			for (MultipartFile file:files)
			{
				Preview preview = new Preview();
				product.getPreviews().add(preview);
				preview.setOriginalFileName(file.getOriginalFilename());
				int dotPos = preview.getOriginalFileName().lastIndexOf(".");
				if (dotPos == -1)
					dotPos = preview.getOriginalFileName().length();
				preview.setFileName(UUID.randomUUID().toString() +
						preview.getOriginalFileName().substring(dotPos));
				preview.setProduct(product);
				preview.setSize(file.getSize());
				file.transferTo(new File(storageLocation, preview.getFileName()));
			}
		}

		return productRepository.save(product);

	}


	public Product updateProduct(Long id, Product product, List<MultipartFile> newFiles) throws IllegalStateException, IOException
	{

		Product fromRep = getProduct(id);

		if (fromRep == null)
			throw new RuntimeException("No such product in the database!");

		fromRep.setId(id);

		// User can not add previews like this, so delete all which are not in the database
	    product.getPreviews().retainAll(fromRep.getPreviews());

	    fromRep.getPreviews().removeAll(product.getPreviews());

		for (Preview p:fromRep.getPreviews())
		{
			File f = new File(storageLocation, p.getFileName());
			f.delete();
		}

		fromRep.copyFieldsFrom(product);

		fromRep.getPreviews().forEach(p -> p.setProduct(fromRep));

		if (newFiles != null && newFiles.size() > 0)
		{
			for (MultipartFile file:newFiles)
			{
				Preview preview = new Preview();
				fromRep.getPreviews().add(preview);
				preview.setOriginalFileName(file.getOriginalFilename());
				int dotPos = preview.getOriginalFileName().lastIndexOf(".");
				if (dotPos == -1)
					dotPos = preview.getOriginalFileName().length();
				preview.setFileName(UUID.randomUUID().toString() +
						preview.getOriginalFileName().substring(dotPos));
				preview.setProduct(fromRep);
				preview.setSize(file.getSize());
				file.transferTo(new File(storageLocation, preview.getFileName()));
			}
		}

		Product saved = productRepository.save(fromRep);

		fillTransientFields(null, List.of(saved));

		return saved;

	}


	public void deleteProduct(Long id)
	{

		Product fromRep = getProduct(id);

		if (fromRep == null)
			throw new RuntimeException("No such product in the database!");

		for (Preview p : fromRep.getPreviews())
		{
			File image = new File(storageLocation, p.getFileName());
			image.delete();
		}

		productRepository.deleteById(id);

	}


	public void removeTagFromProducts(Long tagId)
	{

		getProducts(null, false, null, List.of(tagId), null, Pageable.unpaged())
		.forEach(p -> {
			p.getTags().removeIf(t -> t.getId().equals(tagId));
			productRepository.save(p);
		});

	}



	@PostConstruct
	public void init()
	{

		File storageDir = new File(storageLocation);

		if (!storageDir.exists())
		{
			if (!storageDir.mkdirs())
			{
				throw new RuntimeException("Storage location can not be created!");
			}
		}

		if (!storageDir.canWrite())
		{
			throw new RuntimeException("Storage location is not writable!");
		}

	}



}
