package com.ambraspace.etprodaja.model.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.offer.OfferService;
import com.ambraspace.etprodaja.model.order.Order;
import com.ambraspace.etprodaja.model.order.OrderService;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
public class ReportingService
{


	@Autowired
	private OfferService offerService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${et-prodaja.storage-location}")
	private String imageLocation;



	@Transactional
	public void downloadOffer(String fileName, HttpServletResponse response) throws IOException
	{

		if (!fileName.endsWith(".pdf") && !fileName.endsWith(".xlsx"))
		{
			throw new RuntimeException("No such file!");
		}

		String offerId = fileName.substring(0, fileName.lastIndexOf("."));

		Offer offer = offerService.getOffer(offerId);

		if (offer == null)
			throw new RuntimeException("No such offer!");

		List<Item> items = itemService.getOfferItems(offerId);

		BigDecimal offerGrossValue = BigDecimal.ZERO;

		for (Item i:items)
		{
			offerGrossValue = offerGrossValue
					.add(
							i.getQuantity()
								.multiply(
										i.getGrossPrice()
								)
								.setScale(2, RoundingMode.HALF_EVEN)
					);
		}

		JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(items);

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("offerDate", offer.getOfferDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
		params.put("offerValidity", offer.getValidUntil().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
		params.put("userMobile", offer.getUser().getPhone());
		params.put("userPhone", "051/217-989");
		params.put("userEmail", offer.getUser().getEmail());
		params.put("customerEmail", offer.getContact() != null ? offer.getContact().getEmail() : "");
		params.put("customerName", offer.getCompany().getName());
		params.put("customerPhone", offer.getContact() != null ? offer.getContact().getPhone() : "");
		params.put("customerLocality", offer.getCompany().getLocality());
		params.put("offerNo", offerId);
		params.put("userName", offer.getUser().getFullName());
		params.put("customerContactName", offer.getContact() != null ? offer.getContact().getName() : "");
		params.put("imageLocation", imageLocation);
		params.put("REPORT_LOCALE", java.util.Locale.of("bs", "BA"));
		params.put("notes", "Važnost ponude: "
				+ offer.getValidUntil().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
				+ "\n\n" + offer.getNotes());
		params.put("offerValue", offer.getValue());
		params.put("offerGrossValue", offerGrossValue);
		params.put("vat", offer.getVat());
		params.put("signature", offer.getUser().getSignature());
		params.put("numOfItems", items.size());

		params.put("head", resourceLoader.getResource("classpath:reports/head.png").getURL().toString());
		params.put("foot", resourceLoader.getResource("classpath:reports/foot.png").getURL().toString());

		if (fileName.endsWith(".pdf"))
		{

			response.setContentType(MediaType.APPLICATION_PDF_VALUE);

			try
			{
				InputStream compiledReport = resourceLoader.getResource("classpath:reports/offer-pdf.jasper").getInputStream();
				JasperPrint print = JasperFillManager.fillReport(compiledReport, params, data);
				byte[] pdfFile = JasperExportManager.exportReportToPdf(print);
				response.setContentLengthLong(pdfFile.length);
				response.getOutputStream().write(pdfFile);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (fileName.endsWith(".xlsx")) {

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();)
			{
				InputStream compiledReport = resourceLoader.getResource("classpath:reports/offer-xlsx.jasper").getInputStream();
				JasperPrint print = JasperFillManager.fillReport(compiledReport, params, data);
				JRXlsxExporter exporter = new JRXlsxExporter();
				SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
				config.setDetectCellType(true);
				config.setFitWidth(1);
				config.setSheetNames(new String[] {offer.getId()});
				exporter.setConfiguration(config);
				exporter.setExporterInput(new SimpleExporterInput(print));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
				exporter.exportReport();
				response.setContentLengthLong(byteArrayOutputStream.size());
				response.getOutputStream().write(byteArrayOutputStream.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format not supported!");

		}


	}



	@Transactional
	public void downloadOrder(long orderId, HttpServletResponse response) throws IOException
	{

		Order order = orderService.getOrder(orderId);

		if (order == null)
			throw new RuntimeException("No such order!");

		List<Item> items = itemService.getOrderItems(orderId, false);

		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Narudžba br. " + orderId);

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		headerStyle.setFont(font);

		List<String> columns = List.of("RB", "Ponuda", "Proizvod", "Ref.", "JM", "Kol.", "Cijena", "Vrijednost");

		for (int i=0; i<columns.size(); i++)
		{
			Cell headerCell = header.createCell(i);
			headerCell.setCellValue(columns.get(i));
			headerCell.setCellStyle(headerStyle);
		}

		CellStyle itemStyle = workbook.createCellStyle();
		font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 12);
		font.setBold(false);
		itemStyle.setWrapText(true);
		itemStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		itemStyle.setFont(font);

		for (int i = 0; i < items.size(); i++)
		{
			Row itemRow = sheet.createRow(1 + i);

			Cell currentCell = itemRow.createCell(0);
			currentCell.setCellValue(i + 1);

			currentCell = itemRow.createCell(1);
			currentCell.setCellValue(items.get(i).getOffer().getId());

			currentCell = itemRow.createCell(2);
			currentCell.setCellValue(items.get(i).getProductName());

			currentCell = itemRow.createCell(3);
			currentCell.setCellValue(items.get(i).getStockInfo().getCustomerReference());

			currentCell = itemRow.createCell(4);
			currentCell.setCellValue(items.get(i).getStockInfo().getProduct().getUnit());

			currentCell = itemRow.createCell(5);
			currentCell.setCellValue(items.get(i).getQuantity().doubleValue());

			currentCell = itemRow.createCell(6);
			currentCell.setCellValue(items.get(i).getStockInfo().getUnitPrice().doubleValue());

			currentCell = itemRow.createCell(7);
			currentCell.setCellFormula("F" + (i+2) + "*G" + (i+2));

			for (int j = 0; j < 8; j++)
			{
				itemRow.getCell(j).setCellStyle(itemStyle);
			}

		}

		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 50 * 256);
		sheet.setColumnWidth(3, 30 * 256);
		sheet.setColumnWidth(4, 10 * 256);
		sheet.setColumnWidth(5, 10 * 256);
		sheet.setColumnWidth(6, 15 * 256);
		sheet.setColumnWidth(7, 20 * 256);

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		workbook.write(response.getOutputStream());

		workbook.close();

	}




}
