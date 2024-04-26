package com.ambraspace.etprodaja.model.user;

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
public class UserControllerTestComponent
{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public User getUser(String username) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/users/" + username)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " +  securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		User user = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			user = mapper.readValue(result.getResponse().getContentAsString(), User.class);
		}

		return user;

	}


	public List<User> getUsers() throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/users")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " +  securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(User.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public User addUser(String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/users")
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " +  securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		User user = mapper.readValue(result.getResponse().getContentAsString(), User.class);

		return user;

	}


	public User updateUser(String username, String body) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(put("/api/users/" + username)
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " +  securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		User user = mapper.readValue(result.getResponse().getContentAsString(), User.class);

		return user;

	}


	public void deleteUser(String username) throws Exception
	{

		this.mockMvc.perform(delete("/api/users/" + username)
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " +  securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


}
