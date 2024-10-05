package com.ambraspace.etprodaja.model.deliveryItem;

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

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DeliveryItemControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.findAndRegisterModules();
	}


	public DeliveryItem getDeliveryItem(Long deliveryId, Long id) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/deliveries/" + deliveryId + "/deliveryItems/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		DeliveryItem retVal = null;
		if (!result.getResponse().getContentAsString().isBlank())
		{
			retVal = mapper.readValue(result.getResponse().getContentAsString(), DeliveryItem.class);
		}

		return retVal;

	}


	public List<DeliveryItem> getDeliveryItems(Long deliveryId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/deliveries/" + deliveryId + "/deliveryItems")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(DeliveryItem.class)
				.readValue(result.getResponse().getContentAsString());

	}


	public List<DeliveryItem> addDeliveryItems(Long deliveryId, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/deliveries/" + deliveryId + "/deliveryItems")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(DeliveryItem.class).readValue(result.getResponse().getContentAsString());

	}



	public DeliveryItem updateDeliveryItem(Long deliveryId, Long id, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(put("/api/deliveries/" + deliveryId + "/deliveryItems/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), DeliveryItem.class);

	}



	public void deleteDeliveryItem(Long deliveryId, Long id) throws Exception
	{

		this.mockMvc.perform(delete("/api/deliveries/" + deliveryId + "/deliveryItems/" + id)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler())
		.andReturn();

	}


}

