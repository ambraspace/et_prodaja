package com.ambraspace.etprodaja.model.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProductControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public Product getProduct(Long id) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/products/"+ id)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Product product = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			product = mapper.readValue(result.getResponse().getContentAsString(), Product.class);
		}

		return product;

	}


	public List<Product> getProducts(
			String query,
			Boolean includeComments,
			Long warehouseId,
			List<Long> tagIds,
			Long categoryId) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/products");

		if (query != null)
			builder.param("q", query);

		if (includeComments != null && includeComments)
			builder.param("cm", "true");

		if (warehouseId != null)
			builder.param("w", "" + warehouseId);

		if (tagIds != null && tagIds.size() > 0)
		{
			StringBuffer parameter = new StringBuffer();
			tagIds.forEach(l -> parameter.append(l + ","));
			parameter.delete(parameter.length() - 1, parameter.length());
			builder.param("t", parameter.toString());
		}

		if (categoryId != null)
			builder.param("ct", "" + categoryId);

		MvcResult result =
				this.mockMvc.perform(builder
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(Product.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public byte[] downloadProductPreview(
			Long productId,
			Long previewId) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/products/" + productId + "/previews/" + previewId + "/download");

		MvcResult result =
				this.mockMvc.perform(builder
						.accept(MediaType.APPLICATION_OCTET_STREAM)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return result.getResponse().getContentAsByteArray();

	}


	public Product addProduct(String body, int numFiles) throws Exception
	{

		MockMultipartHttpServletRequestBuilder builder =
				multipart(HttpMethod.POST, "/api/products")
				.file(new MockMultipartFile(
						"product",
						"product",
						MediaType.APPLICATION_JSON_VALUE,
						body.getBytes(StandardCharsets.UTF_8)));

		for (int i = 0; i < numFiles; i++)
		{
			builder.file(new MockMultipartFile(
					"files",
					"file " + (i + 1) + ".png",
					MediaType.IMAGE_PNG_VALUE,
					new byte[] {0}));
		}

		MvcResult result =
				this.mockMvc.perform(builder
						//.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Product product = mapper.readValue(result.getResponse().getContentAsString(), Product.class);

		return product;

	}


	public Product updateProduct(Long id, String body, int numFiles) throws Exception
	{

		MockMultipartHttpServletRequestBuilder builder =
				multipart(HttpMethod.PUT, "/api/products/" + id)
				.file(new MockMultipartFile(
						"product",
						"product",
						MediaType.APPLICATION_JSON_VALUE,
						body.getBytes(StandardCharsets.UTF_8)));

		for (int i = 0; i < numFiles; i++)
		{
			builder.file(new MockMultipartFile(
					"files",
					"file 1.png",
					MediaType.IMAGE_PNG_VALUE,
					new byte[] {0}));
		}

		MvcResult result =
				this.mockMvc.perform(builder
						//.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Product product = mapper.readValue(result.getResponse().getContentAsString(), Product.class);

		return product;

	}


	public void deleteProduct(Long id) throws Exception
	{

		this.mockMvc.perform(delete("/api/products/" + id)
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


}
