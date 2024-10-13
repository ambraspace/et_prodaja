package com.ambraspace.etprodaja.model.delivery;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.ambraspace.etprodaja.model.delivery.Delivery.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DeliveryControllerTestComponent
{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.findAndRegisterModules();
	}


	public Delivery getDelivery(String id) throws Exception
	{

		MvcResult result = this.mockMvc.perform(get("/api/deliveries/" + id)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Delivery delivery = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			delivery = mapper.readValue(result.getResponse().getContentAsString(), Delivery.class);
		}

		return delivery;

	}


	public List<Delivery> getDeliveries(Long companyId, Status status) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/deliveries");

		if (companyId != null)
			builder.param("c", "" + companyId);

		if (status != null)
			builder.param("s", status.name());

		MvcResult result = this.mockMvc.perform(builder
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(Delivery.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public Delivery addDelivery(String body) throws Exception
	{

		MvcResult result = this.mockMvc.perform(post("/api/deliveries")
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Delivery.class);

	}


	public Delivery updateDelivery(String id, String body) throws Exception
	{

		MvcResult result = this.mockMvc.perform(put("/api/deliveries/" + id)
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Delivery.class);

	}


	public void deleteDelivery(String id) throws Exception
	{

		this.mockMvc.perform(delete("/api/deliveries/" + id)
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler());

	}


	public Delivery setDelivered(String id) throws Exception
	{

		MvcResult result = this.mockMvc.perform(put("/api/deliveries/" + id + "/delivered")
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Delivery.class);

	}


	public void deleteAllDeliveries() throws Exception
	{

		this.mockMvc.perform(delete("/api/deliveries/all")
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler());

	}


}
