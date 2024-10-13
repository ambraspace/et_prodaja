package com.ambraspace.etprodaja.model.item;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ItemControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.findAndRegisterModules();
	}


	public Item getOfferItem(String offerId, Long itemId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/offers/" + offerId + "/items/" + itemId)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Item retVal = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			retVal = mapper.readValue(result.getResponse().getContentAsString(), Item.class);
		}

		return retVal;

	}


	public List<Item> getOfferItems(String offerId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/offers/" + offerId + "/items")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(Item.class)
				.readValue(result.getResponse().getContentAsString());

	}


	public List<Item> getOrderItems(String orderId, boolean onlyUndelivered) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/orders/" + orderId + "/items")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt());

		if (onlyUndelivered)
			builder.param("ou", "true");

		MvcResult result =
				this.mockMvc.perform(builder)
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(Item.class)
				.readValue(result.getResponse().getContentAsString());

	}


	public List<Item> addItems(String offerNo, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/offers/" + offerNo + "/items")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(Item.class).readValue(result.getResponse().getContentAsString());

	}


	public List<Item> updateItems(String offerNo, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(put("/api/offers/" + offerNo + "/items")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(Item.class).readValue(result.getResponse().getContentAsString());

	}


	public void deleteItem(String offerNo, Long itemId) throws Exception
	{

		this.mockMvc.perform(delete("/api/offers/" + offerNo + "/items/" + itemId)
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}

}
