package com.ambraspace.etprodaja.model.category;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class CategoryControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public List<Category> getCategories() throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/categories")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		List<Category> categories = mapper.readerForListOf(Category.class)
				.readValue(result.getResponse().getContentAsString());

		return categories;

	}



	public List<Category> saveCategories(String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/categories")
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		List<Category> categories = mapper.readerForListOf(Category.class)
				.readValue(result.getResponse().getContentAsString());

		return categories;

	}

}
