package com.ambraspace.etprodaja.model.offer;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.ambraspace.etprodaja.model.offer.Offer.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OfferControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.findAndRegisterModules();
	}


	public Offer getOffer(String offerNo) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(get("/api/offers/" + offerNo)
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		Offer offer = null;

		if (!result.getResponse().getContentAsString().isBlank())
		{
			offer = mapper.readValue(result.getResponse().getContentAsString(), Offer.class);
		}

		return offer;

	}


	public List<Offer> getOffers(String username, Long companyId, Status status, boolean onlyOverdue) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/offers");

		if (username != null)
			builder.param("u", username);

		if (companyId != null)
			builder.param("c", "" + companyId);

		if (status != null)
			builder.param("s", status.name());

		if (onlyOverdue)
			builder.param("o", "true");

		MvcResult result =
				this.mockMvc.perform(builder
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		JSONObject page = new JSONObject(result.getResponse().getContentAsString());

		return mapper.readerForListOf(Offer.class)
				.readValue(page.getJSONArray("content").toString());

	}


	public Offer addOffer(String body, String username) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/offers")
						.with(csrf())
						.with(user(username))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Offer.class);

	}


	public Offer updateOffer(String offerNo, String body, String username) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(put("/api/offers/" + offerNo)
						.with(csrf())
						.with(user(username))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Offer.class);

	}


	public void deleteOffer(String offerNo) throws Exception
	{

		this.mockMvc.perform(delete("/api/offers/" + offerNo)
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}


	public Offer cancelOffer(String offerNo, String reason) throws Exception
	{

		MockHttpServletRequestBuilder builder = patch("/api/offers/" + offerNo + "/cancel");

		if (reason != null)
			builder.param("r", reason);

		MvcResult result =
				this.mockMvc.perform(builder
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Offer.class);

	}


	public Offer acceptOffer(String offerNo) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(patch("/api/offers/" + offerNo + "/accept")
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Offer.class);

	}


	public Offer duplicateOffer(String offerId, String username) throws Exception
	{

		MvcResult result =
				this.mockMvc.perform(post("/api/offers/" + offerId + "/duplicate")
						.with(csrf())
						.with(user(username))
						.accept(MediaType.APPLICATION_JSON)
						.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return mapper.readValue(result.getResponse().getContentAsString(), Offer.class);

	}


	public void deleteAllOffers() throws Exception
	{

		this.mockMvc.perform(delete("/api/offers/all")
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("X-XSRF-TOKEN", securityTestComponent.getXsrf())
				.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
		.andExpect(status().isOk())
		.andDo(securityTestComponent.getResultHandler());

	}

}
