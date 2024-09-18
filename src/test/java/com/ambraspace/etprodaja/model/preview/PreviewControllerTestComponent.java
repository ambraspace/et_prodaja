package com.ambraspace.etprodaja.model.preview;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PreviewControllerTestComponent {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityTestComponent;

	private static final ObjectMapper mapper = new ObjectMapper();


	public byte[] downloadProductPreview(
			String fileName) throws Exception
	{

		MockHttpServletRequestBuilder builder = get("/api/images/" + fileName);

		MvcResult result =
				this.mockMvc.perform(builder
						.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		return result.getResponse().getContentAsByteArray();

	}


	public List<Preview> addPreviews(int numFiles) throws Exception
	{

		MockMultipartHttpServletRequestBuilder builder =
				multipart(HttpMethod.POST, "/api/previews");

		for (int i = 0; i < numFiles; i++)
		{
			builder.file(new MockMultipartFile(
					"files",
					"file " + (i + 1) + ".png",
					MediaType.IMAGE_PNG_VALUE,
					new byte[] {0}));
		}

		MvcResult result =
				this.mockMvc.perform(builder
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + securityTestComponent.getJwt()))
				.andExpect(status().isOk())
				.andDo(securityTestComponent.getResultHandler())
				.andReturn();

		List<Preview> previews = mapper.readerForListOf(Preview.class).readValue(result.getResponse().getContentAsString());

		return previews;

	}


}
