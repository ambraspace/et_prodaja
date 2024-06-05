package com.ambraspace.etprodaja.model.tag;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class TagControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public Tag getTag(String id) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/tags/" + id)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Tag tag = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			tag = mapper.readValue(result.getResponse().getContentAsString(), Tag.class);
		}

		return tag;

	}


	public List<Tag> getTags() throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/tags")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(Tag.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public List<Tag> searchTags(String query) throws Exception
	{
		MvcResult result =
				this.mockMvc.perform(get("/api/tags/search")
						.param("q", query)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		List<Tag> tags = mapper.readerForListOf(Tag.class)
				.readValue(result.getResponse().getContentAsString());

		return tags;

	}


	public Tag addTag(String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/tags")
						//with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Tag tag = mapper.readValue(result.getResponse().getContentAsString(), Tag.class);

		return tag;

	}


	public void deleteTag(String id) throws Exception
	{

		this.mockMvc.perform(delete("/api/tags/" + id)
				//.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				//.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


}
