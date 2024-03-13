package com.ambraspace.etprodaja.model.product;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ambraspace.etprodaja.model.category.CategoryService;
import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;
import com.ambraspace.etprodaja.model.stockinfo.StockInfoService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Tuple;

@Service
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
	private ItemService itemService;


	@Transactional(readOnly = true)
	public Page<Product> getProducts(String query, Boolean includeComments, Long warehouseId, List<Long> tagIds, Long categoryId, Pageable pageable)
	{

		Page<Long> productIds;

		if (warehouseId != null && warehouseId > 0)
		{

			if (query == null || query.trim().equals(""))
			{
				if (tagIds != null && tagIds.size() > 0)
				{
					if (categoryId != null)
					{
						//Search by warehouse, tags and category
						productIds = productRepository.findByWarehouseTagsAndCategory(warehouseId, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						//Search by warehouse and tags
						productIds = productRepository.findByWarehouseAndTags(warehouseId, tagIds, pageable);
					}
				} else {
					if (categoryId != null)
					{
						// Search by warehouse and category
						productIds = productRepository.findByWarehouseAndCategory(warehouseId, categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						// Search by warehouse
						productIds = productRepository.findByWarehouse(warehouseId, pageable);
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
							productIds = productRepository.findByWarehouseNameCommentTagsAndCategory(warehouseId, query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse, name, comment and tags
							productIds = productRepository.findByWarehouseNameCommentAndTags(warehouseId, query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by warehouse, name, comment and category
							productIds = productRepository.findByWarehouseNameCommentAndCategory(warehouseId, query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse, name and comment
							productIds = productRepository.findByWarehouseNameAndComment(warehouseId, query, pageable);
						}
					}
				} else {
					if (tagIds != null && tagIds.size() > 0)
					{
						if (categoryId != null)
						{
							//Search by warehouse, name, tags and category
							productIds = productRepository.findByWarehouseNameTagsAndCategory(warehouseId, query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse, name and tags
							productIds = productRepository.findByWarehouseNameAndTags(warehouseId, query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by warehouse, name and category
							productIds = productRepository.findByWarehouseNameAndCategory(warehouseId, query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by warehouse and name
							productIds = productRepository.findByWarehouseAndName(warehouseId, query, pageable);
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
						productIds = productRepository.findByTagsAndCategory(tagIds, categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						//Search by tags
						productIds = productRepository.findByTags(tagIds, pageable);
					}
				} else {
					if (categoryId != null)
					{
						// Search by category
						productIds = productRepository.findByCategory(categoryService.collectCategoryIds(categoryId), pageable);
					} else {
						// Search all
						productIds = productRepository.findAllProducts(pageable);
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
							productIds = productRepository.findByNameCommentTagsAndCategory(query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name, comment and tags
							productIds = productRepository.findByNameCommentAndTags(query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by name, comment and category
							productIds = productRepository.findByNameCommentAndCategory(query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name and comment
							productIds = productRepository.findByNameAndComment(query, pageable);
						}
					}
				} else {
					if (tagIds != null && tagIds.size() > 0)
					{
						if (categoryId != null)
						{
							//Search by name, tags and category
							productIds = productRepository.findByNameTagsAndCategory(query, tagIds, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name and tags
							productIds = productRepository.findByNameAndTags(query, tagIds, pageable);
						}
					} else {
						if (categoryId != null)
						{
							//Search by name and category
							productIds = productRepository.findByNameAndCategory(query, categoryService.collectCategoryIds(categoryId), pageable);
						} else {
							//Search by name
							productIds = productRepository.findByName(query, pageable);
						}
					}
				}
			}
		}

		List<Product> retVal = new ArrayList<Product>();

		if (productIds.hasContent())
		{
			retVal = getProductsWithPreviews(productIds.getContent(), pageable.getSort());
			fillTransientFields(warehouseId, retVal);
		}

		return new PageImpl<Product>(retVal, pageable, productIds.getTotalElements());

	}


	@Transactional(readOnly = true)
	public Product getProduct(Long id)
	{
		Product p = productRepository.getProductWithPreviews(id).orElse(null);
		if (p != null)
			productRepository.getProductWithCategoryAndTags(id).orElse(null);

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
			List<Tuple> itemDataForOffers = itemService.getItemDataForProductsInValidOffersByWarehouse(warehouseId, products);
			List<Tuple> itemDataForOrders = itemService.getItemDataForOrderedProductsByWarehouse(warehouseId, products);

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
			List<Tuple> itemDataForOffers = itemService.getItemDataForProductsInValidOffers(products);
			List<Tuple> itemDataForOrders = itemService.getItemDataForOrderedProducts(products);

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


	private List<Product> getProductsWithPreviews(List<Long> productIds, Sort sort)
	{
		List<Product> retVal = new ArrayList<Product>();
		productRepository.getProductsById(productIds, sort).forEach(retVal::add);
		return retVal;
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


	@Transactional
	public Product updateProduct(Long id, Product product, List<MultipartFile> newFiles) throws IllegalStateException, IOException
	{

		Product fromRep = getProduct(id);

		if (fromRep == null)
			throw new RuntimeException("No such product in the database!");

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


	@Transactional
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


	@Transactional
	public void removeTagFromProducts(Long tagId)
	{

		List<Product> modifiedProducts = new ArrayList<Product>();

		getProducts(null, false, null, List.of(tagId), null, Pageable.unpaged())
		.forEach(p -> {
			p.getTags().removeIf(t -> t.getId().equals(tagId));
			modifiedProducts.add(p);
		});

		if (modifiedProducts.size() > 0)
			productRepository.saveAll(modifiedProducts);

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
