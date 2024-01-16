package com.ambraspace.etprodaja.model.warehouse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class WarehouseControllerTests
{

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void addStockInfo() throws Exception
	{

		this.mockMvc
			.perform(post("/api/stockInfo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
{
	"warehouse":{"id":17},
	"product":{"id":89},
	"customerReference":"Neki njihov naziv",
	"quantity":25.1,
	"unitPrice":3.14
}
				"""))
			.andExpect(status().isOk());


	}

}
