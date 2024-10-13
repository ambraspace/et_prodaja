package com.ambraspace.etprodaja.model.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.ambraspace.etprodaja.model.order.Order.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderControllerTestComponent
{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.findAndRegisterModules();
	}


	public Order getOrder(String id) throws Exception
	{

		MvcResult result = this.mockMvc.perform(get("/api/orders/" + id)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Order order = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			order = mapper.readValue(result.getResponse().getContentAsString(), Order.class);
		}

		return order;

	}


	public List<Order> getOrders(Long warehouseId, Status status, boolean onlyUndelivered) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/orders");

		if (warehouseId != null)
			builder.param("w", "" + warehouseId);

		if (status != null)
			builder.param("s", status.name());

		if (onlyUndelivered)
			builder.param("u", "true");

		MvcResult result = this.mockMvc.perform(builder
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(Order.class).readValue(page.getJSONArray("content").toString());

	}


	public Order closeOrder(String id) throws Exception
	{

		MvcResult result = this.mockMvc.perform(put("/api/orders/" + id + "/close")
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Order.class);

	}


	public void deleteAllOrders() throws Exception
	{

		this.mockMvc.perform(delete("/api/orders/all")
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


}
