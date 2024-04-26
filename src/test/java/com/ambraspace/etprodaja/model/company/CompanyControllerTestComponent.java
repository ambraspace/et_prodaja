package com.ambraspace.etprodaja.model.company;

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
public class CompanyControllerTestComponent
{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;


	private static final ObjectMapper mapper = new ObjectMapper();


	public Company getCompany(Long id) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/companies/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andDo(securityTestComponent.getResultHandler())
				.andExpect(status().isOk())
				.andReturn();

			Company company = null;

			if (!result.getResponse().getContentAsString().isBlank())
			{
				company =
						mapper.readValue(result.getResponse().getContentAsString(), Company.class);
			}

			return company;

	}


	public List<Company> getCompanies() throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/companies")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(Company.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public Company addCompany(String body) throws Exception
	{

		MvcResult result =
			this.mockMvc.perform(post("/api/companies")
					.with(csrf())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(body)
					.header("Authorization", "Bearer " + securityTestComponent.getJwt())
					.header("X-XSRF-TOKEN", securityTestComponent.getXsrf()))
			.andDo(securityTestComponent.getResultHandler())
			.andExpect(status().isOk())
			.andReturn();

		Company company =
				mapper.readValue(result.getResponse().getContentAsString(), Company.class);

		return company;

	}


	public Company updateCompany(Long id, String body) throws Exception
	{

		MvcResult result =
			this.mockMvc.perform(put("/api/companies/" + id)
					.with(csrf())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(body)
					.header("Authorization", "Bearer " + securityTestComponent.getJwt())
					.header("X-XSRF-TOKEN", securityTestComponent.getXsrf()))
			.andDo(securityTestComponent.getResultHandler())
			.andExpect(status().isOk())
			.andReturn();

		Company company =
				mapper.readValue(result.getResponse().getContentAsString(), Company.class);

		return company;

	}


	public void deleteCompany(Long id) throws Exception
	{

		this.mockMvc.perform(delete("/api/companies/" + id)
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt())
				.header("X-XSRF-TOKEN", securityTestComponent.getXsrf()))
		.andDo(securityTestComponent.getResultHandler())
		.andExpect(status().isOk());

	}

}
