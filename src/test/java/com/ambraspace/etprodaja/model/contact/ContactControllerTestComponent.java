package com.ambraspace.etprodaja.model.contact;

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
public class ContactControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public Contact getContact(Long companyId, Long contactId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/companies/" + companyId + "/contacts/" + contactId)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andDo(securityTestComponent.getResultHandler())
				.andExpect(status().isOk())
				.andReturn();

			Contact contact = null;

			if (!result.getResponse().getContentAsString().isBlank())
			{
				contact = mapper.readValue(result.getResponse().getContentAsString(), Contact.class);
			}

			return contact;

	}


	public List<Contact> getContacts(Long companyId) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/companies/" + companyId + "/contacts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readerForListOf(Contact.class)
				.readValue(result.getResponse().getContentAsString());

	}


	public Contact addContact(Long companyId, String body) throws Exception
	{

		MvcResult result =
			this.mockMvc.perform(post("/api/companies/" + companyId + "/contacts")
					.with(csrf())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(body)
					.header("Authorization", "Bearer " + securityTestComponent.getJwt())
					.header("X-XSRF_TOKEN", securityTestComponent.getXsrf()))
			.andDo(securityTestComponent.getResultHandler())
			.andExpect(status().isOk())
			.andReturn();

		Contact contact =
				mapper.readValue(result.getResponse().getContentAsString(), Contact.class);

		return contact;

	}


	public Contact updateContact(Long companyId, Long contactId, String body) throws Exception
	{

		MvcResult result =
			this.mockMvc.perform(put("/api/companies/" + companyId + "/contacts/" + contactId)
					.with(csrf())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(body)
					.header("Authorization", "Bearer " + securityTestComponent.getJwt())
					.header("X-XSRF_TOKEN", securityTestComponent.getXsrf()))
			.andDo(securityTestComponent.getResultHandler())
			.andExpect(status().isOk())
			.andReturn();

		Contact contact =
				mapper.readValue(result.getResponse().getContentAsString(), Contact.class);

		return contact;

	}


	public void deleteContact(Long companyId, Long contactId) throws Exception
	{

		this.mockMvc.perform(delete("/api/companies/" + companyId + "/contacts/" + contactId)
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + securityTestComponent.getJwt())
				.header("X-XSRF_TOKEN", securityTestComponent.getXsrf()))
		.andDo(securityTestComponent.getResultHandler())
		.andExpect(status().isOk());

	}

}
