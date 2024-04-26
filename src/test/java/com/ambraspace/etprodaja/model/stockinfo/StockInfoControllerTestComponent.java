package com.ambraspace.etprodaja.model.stockinfo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StockInfoControllerTestComponent
{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public StockInfo getStockInfo(Long productId, Long id) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/products/" + productId + "/stockInfos/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		StockInfo stockInfo = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			stockInfo = mapper.readValue(result.getResponse().getContentAsString(), StockInfo.class);
		}

		return stockInfo;

	}


	public List<StockInfo> getStockInfosByProduct(Long productId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/products/" + productId + "/stockInfos")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(StockInfo.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public StockInfo addStockInfo(Long productId, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/products/" + productId + "/stockInfos")
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), StockInfo.class);

	}


	public StockInfo updateStockInfo(Long productId, Long id, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(put("/api/products/" + productId + "/stockInfos/" + id)
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), StockInfo.class);

	}


	public void deleteStockInfo(Long productId, Long id) throws Exception
	{

		this.mockMvc.perform(delete("/api/products/" + productId + "/stockInfos/" + id)
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


}
