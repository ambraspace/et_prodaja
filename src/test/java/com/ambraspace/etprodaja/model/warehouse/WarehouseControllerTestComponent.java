package com.ambraspace.etprodaja.model.warehouse;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
public class WarehouseControllerTestComponent
{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public Warehouse getWarehouse(Long companyId, Long warehouseId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(
						get("/api/companies/" + companyId + "/warehouses/" + warehouseId)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Warehouse warehouse = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			warehouse = mapper.readValue(result.getResponse().getContentAsString(), Warehouse.class);
		}

		return warehouse;

	}


	public List<Warehouse> getWarehouses(Long companyId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(
						get("/api/companies/" + companyId + "/warehouses")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(Warehouse.class)
				.readValue(result.getResponse().getContentAsString());

	}


	public Warehouse addWarehouse(Long companyId, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/companies/" + companyId + "/warehouses")
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt())
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Warehouse.class);

	}


	public Warehouse updateWarehouse(Long companyId, Long warehouseId, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(put("/api/companies/" + companyId + "/warehouses/" + warehouseId)
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt())
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Warehouse.class);

	}


	public void deleteWarehouse(Long companyId, Long warehouseId) throws Exception
	{

		this.mockMvc.perform(delete("/api/companies/" + companyId + "/warehouses/" + warehouseId)
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt())
				.header("X-XSRF-TOKEN", securityTestComponent.getXsrf()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


}
