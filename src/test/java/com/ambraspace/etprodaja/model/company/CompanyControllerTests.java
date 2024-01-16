package com.ambraspace.etprodaja.model.company;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTests
{

	@Autowired
	private MockMvc mockMvc;


	@Test
	public void testAll() throws Exception
	{


		MvcResult res = this.mockMvc.perform(
				post("/api/companies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(""))
			.andExpect(status().isBadRequest())
			.andReturn();

		assertTrue(res.getResolvedException() instanceof HttpMessageNotReadableException);


		res = this.mockMvc.perform(
				post("/api/companies")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id":0,
							"name":"Test company",
							"locality":"Test locality",
							"contacts":[
								{
									"name":"Test contact"
								}
							]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value("Test company"))
			.andExpect(jsonPath("locality").value("Test locality"))
			.andExpect(jsonPath("id").isNumber())
			.andExpect(jsonPath("contacts").isArray())
			.andReturn();

		Long id = new JSONObject(res.getResponse().getContentAsString()).getLong("id");


		this.mockMvc.perform(
				post("/api/companies")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"name":"T",
							"locality":"Test locality"
						}
						"""))
			.andExpect(status().isBadRequest());


		this.mockMvc.perform(
				get("/api/companies")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());


		this.mockMvc.perform(
				get("/api/companies")
				.param("sort", "no_field_name")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());


		this.mockMvc.perform(
				get("/api/companies/" + id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());


		this.mockMvc.perform(
				get("/api/companies/null")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());


		this.mockMvc.perform(
				put("/api/companies/" + (id+1))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id":0,
							"name":"Test company 2",
							"locality":"Test locality 2"
						}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("No such company in the database!"));


		this.mockMvc.perform(
				put("/api/companies/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id":0,
							"name":"Test company 2",
							"locality":"Test locality 2"
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value("Test company 2"))
			.andExpect(jsonPath("locality").value("Test locality 2"))
			.andExpect(jsonPath("id").value(id));


		this.mockMvc.perform(
				put("/api/companies/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id":0,
							"name":"Test company 3",
							"locality":"Test locality 3",
							"contacts":[
								{
									"name":"Test contact 3"
								}
							]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value("Test company 3"))
			.andExpect(jsonPath("locality").value("Test locality 3"))
			.andExpect(jsonPath("id").value(id));


		this.mockMvc.perform(
				delete("/api/companies/" + (id+1))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("No such company in the database!"));


		this.mockMvc.perform(
				delete("/api/companies/" + id)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());


	}

}
