package com.ambraspace.etprodaja.model.preview;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.product.Product;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class PreviewService
{

	private static final Logger logger = LoggerFactory.getLogger(PreviewService.class);

	@Autowired
	private PreviewRepository previewRepository;

	@Autowired
	private ItemService itemService;

	@Value("${et-prodaja.storage-location}")
	private String storageLocation;


	@Transactional
	public List<Preview> addPreviews(List<MultipartFile> files) throws IllegalStateException, IOException
	{

		List<Preview> retVal = new ArrayList<Preview>();

		if (files != null && files.size() > 0)
		{

			for (MultipartFile file:files)
			{
				Preview preview = new Preview();
				preview.setOriginalFileName(file.getOriginalFilename());
				int dotPos = preview.getOriginalFileName().lastIndexOf(".");
				if (dotPos == -1)
					dotPos = preview.getOriginalFileName().length();
				preview.setFileName(UUID.randomUUID().toString() +
						preview.getOriginalFileName().substring(dotPos));
				preview.setSize(file.getSize());
				file.transferTo(new File(storageLocation, preview.getFileName()));
				retVal.add(previewRepository.save(preview));
			}

		}

		return retVal;

	}


	@Transactional
	public List<Preview> linkToProduct(List<Preview> previews, Product product)
	{

		List<Preview> retVal = new ArrayList<Preview>();

		previews.forEach(pr -> pr.setProduct(product));

		previewRepository.saveAll(previews).forEach(retVal::add);

		return retVal;

	}


	@Transactional
	public List<Preview> unlinkPreviews(List<Preview> previews)
	{

		List<Preview> retVal = new ArrayList<Preview>();

		previews.forEach(pr -> pr.setProduct(null));

		previewRepository.saveAll(previews).forEach(retVal::add);

		return retVal;

	}


	public void downloadProductImage(String fileName, HttpServletResponse response) throws IOException
	{

		File file = new File(storageLocation, fileName);

		if (file.isDirectory() || !file.exists())
			throw new RuntimeException("File not found!");

		String contentType = Files.probeContentType(file.toPath());

        if (contentType == null) {
            // Use the default media type
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        response.setContentType(contentType);

        response.setContentLengthLong(Files.size(file.toPath()));

        Files.copy(file.toPath(), response.getOutputStream());

	}


	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deleteOrphanPreviews()
	{

		logger.info("Deleting orphan previews...");

		List<Preview> orphans = new ArrayList<Preview>();
		previewRepository.findByProductIsNull().forEach(pr -> {
			orphans.add(pr);
		});
		previewRepository.deleteAll(orphans);

		Set<String> images = new HashSet<String>();
		Set<String> imagesInTheDb = new HashSet<String>();

		File directory = new File(storageLocation);
		List.of(directory.list()).forEach(images::add);

		previewRepository.findAll().forEach(pr -> imagesInTheDb.add(pr.getFileName()));

		imagesInTheDb.addAll(itemService.getItemPreviews());

		images.removeAll(imagesInTheDb);

		images.forEach(f -> {
			new File(storageLocation, f).delete();
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
