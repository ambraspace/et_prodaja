package com.ambraspace.etprodaja.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests
{

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testProductOperations() throws Exception
	{

		/*
		 * Add tags
		 */

		MvcResult res = this.mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
{
  "id": 0,
  "name": "Tag 1",
  "color": "#111111"
}
						"""))
		.andExpect(jsonPath("id").isNumber())
		.andExpect(status().isOk())
		.andReturn();

		JSONObject jsonObject = new JSONObject(res.getResponse().getContentAsString());

		Long tagId = jsonObject.getLong("id");

		res = this.mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
{
  "id": 0,
  "name": "Tag 2",
  "color": "#222222"
}
						"""))
		.andExpect(jsonPath("id").isNumber())
		.andExpect(status().isOk())
		.andReturn();


		/*
		 * Get a tag by its ID
		 */


		res = this.mockMvc.perform(get("/api/tags/" + tagId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		jsonObject = new JSONObject(res.getResponse().getContentAsString());

		assertEquals(jsonObject.getLong("id"), tagId);
		assertEquals(jsonObject.getString("name"), "Tag 1");
		assertEquals(jsonObject.getString("color"), "#111111");


		/*
		 * Save categories
		 */


		res = this.mockMvc.perform(post("/api/categories")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
[
  {
    "id": 1,
    "parentId": 0,
    "name": "Kategorija 1"
  },
  {
    "id": 3,
    "parentId": 2,
    "name": "Kategorija 1.1.1"
  },
  {
    "id": 2,
    "parentId": 1,
    "name": "Kategorija 1.1"
  },
  {
    "id": 4,
    "parentId": 0,
    "name": "Kategorija 2"
  },
  {
    "id": 6,
    "parentId": 0,
    "name": "Kategorija 3"
  },
  {
    "id": 5,
    "parentId": 4,
    "name": "Kategorija 2.1"
  }
]
						"""))
				.andExpect(status().isOk())
				.andReturn();


		/*
		 * Get all categories
		 */


		res = this.mockMvc.perform(get("/api/categories")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		JSONArray jsonArray = new JSONArray(res.getResponse().getContentAsString());

		assertEquals(jsonArray.length(), 6);


		/*
		 * Save categories
		 */


		res = this.mockMvc.perform(post("/api/categories")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
[
  {
    "id": 1,
    "parentId": 0,
    "name": "Kategorija 1"
  },
  {
    "id": 3,
    "parentId": 2,
    "name": "Kategorija 1.1.1"
  },
  {
    "id": 2,
    "parentId": 1,
    "name": "Kategorija 1.1"
  }
]
						"""))
				.andExpect(status().isOk())
				.andReturn();


		/*
		 * Get all categories
		 */


		res = this.mockMvc.perform(get("/api/categories")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		jsonArray = new JSONArray(res.getResponse().getContentAsString());

		assertEquals(jsonArray.length(), 3);


		/*
		 * Add a product
		 */

		jsonObject = new JSONObject("""
{
  "id": 0,
  "name": "Ukras 1",
  "previews": [
    {
      "id": 0,
      "fileName": "string",
      "originalFileName": "string",
      "size": 0,
      "primary": true
    }
  ],
  "unit": "kom.",
  "price": 100,
  "category": {
    "id": 3,
    "parentId": 0,
    "name": "string"
  },
  "tags": [
    {
      "id": 1,
      "name": "string",
      "color": "#DC6c5B"
    }
  ],
  "comment": "Novi ukras neizmijenjen"
}
				""");

		jsonObject.getJSONArray("tags").getJSONObject(0).put("id", tagId);

		res = this.mockMvc.perform(
				multipart("/api/products")
				.file(
						new MockMultipartFile(
								"product",
								"product",
								MediaType.APPLICATION_JSON_VALUE,
								jsonObject.toString().getBytes(StandardCharsets.UTF_8)
								))
				.file(
						new MockMultipartFile(
								"files",
								"file1.png",
								MediaType.IMAGE_PNG_VALUE,
								new byte[] {1}))
				.file(
						new MockMultipartFile(
								"files",
								"file2.png",
								MediaType.IMAGE_PNG_VALUE,
								new byte[] {2}))
				.file(
						new MockMultipartFile(
								"files",
								"file3.png",
								MediaType.IMAGE_PNG_VALUE,
								new byte[] {3}))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").isNumber())
			.andReturn();

		jsonObject = new JSONObject(res.getResponse().getContentAsString());

		Long productId = jsonObject.getLong("id");


		/*
		 * Update a product
		 */


		jsonObject.put("name", "Ukras 1.1");
		jsonObject.getJSONArray("previews").remove(2);
		jsonObject.put("price", 110);
		jsonObject.getJSONObject("category").put("id", 2);
		jsonObject.getJSONArray("tags").getJSONObject(0).put("id", (tagId + 1));
		jsonObject.put("comment", "Ukras 1 izmijenjen");

		res = this.mockMvc.perform(
				multipart(HttpMethod.PUT, "/api/products/" + productId)
				.file(
						new MockMultipartFile(
								"product",
								"product",
								MediaType.APPLICATION_JSON_VALUE,
								jsonObject.toString().getBytes(StandardCharsets.UTF_8)
								))
				.file(
						new MockMultipartFile(
								"files",
								"file4.png",
								MediaType.IMAGE_PNG_VALUE,
								new byte[] {4}))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		jsonObject = new JSONObject(res.getResponse().getContentAsString());

		assertEquals(jsonObject.getLong("id"), productId);
		assertEquals(jsonObject.getString("name"), "Ukras 1.1");
		assertEquals(jsonObject.getJSONArray("previews").getJSONObject(0).getString("originalFileName"), "file1.png");
		assertEquals(jsonObject.getJSONArray("previews").getJSONObject(1).getString("originalFileName"), "file2.png");
		assertEquals(jsonObject.getJSONArray("previews").getJSONObject(2).getString("originalFileName"), "file4.png");
		assertEquals(jsonObject.getInt("price"), 110);
		assertEquals(jsonObject.getJSONObject("category").getLong("id"), 2);
		assertEquals(jsonObject.getJSONArray("tags").getJSONObject(0).getLong("id"), (tagId + 1));
		assertEquals(jsonObject.getString("comment"), "Ukras 1 izmijenjen");


		/*
		 * Delete a product
		 */

	    this.mockMvc.perform(delete("/api/products/" + productId))
	    	.andExpect(status().isOk());


		res = this.mockMvc.perform(post("/api/categories")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
[]
						"""))
				.andExpect(status().isOk())
				.andReturn();


		/*
		 * Get all categories
		 */


		res = this.mockMvc.perform(get("/api/categories")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		jsonArray = new JSONArray(res.getResponse().getContentAsString());

		assertEquals(jsonArray.length(), 0);


		/*
		 * Get all tags
		 */


		res = this.mockMvc.perform(get("/api/tags")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		jsonObject = new JSONObject(res.getResponse().getContentAsString());

		assertEquals(jsonObject.getJSONArray("content").length(), 2);


		/*
		 * Delete a tag by its ID
		 */

		for (int i = 0; i < jsonObject.getJSONArray("content").length(); i++)
		{
			long id = jsonObject.getJSONArray("content").getJSONObject(i).getLong("id");
			this.mockMvc.perform(delete("/api/tags/" + id))
					.andExpect(status().isOk());
		}


	}



}
