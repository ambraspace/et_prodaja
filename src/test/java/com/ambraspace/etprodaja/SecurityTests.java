package com.ambraspace.etprodaja;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class SecurityTests {


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityTestComponent securityService;

	@Test
	@Order(1)
	public void authenticate() throws Exception
	{
		securityService.authenticate("admin", "administrator");
	}


	@Test
	@Order(2)
	public void testUnauthenticatedAccess() throws Exception
	{
		this.mockMvc.perform(get("/api/user")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnauthorized());
	}


	@Test
	@Order(3)
	public void testAuthenticatedGetCall() throws Exception
	{
			this.mockMvc.perform(get("/api/user")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + securityService.getJwt()))
			.andExpect(status().isOk()).andDo(securityService.getResultHandler());
	}


	@Test
	@Order(4)
	public void testPostCallWithoutCsrf() throws Exception
	{

		String body = String.format("""
{
  "username":"%s",
  "password":"%s"
}
				""", "admin", "administrator");

		this.mockMvc.perform(post("/authenticate")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isForbidden()).andDo(securityService.getResultHandler());

	}


	@Test
	@Order(5)
	public void refreshToken() throws Exception
	{

		String oldJwt = new String(securityService.getJwt());

		Thread.sleep(Duration.ofSeconds(1));

		this.mockMvc.perform(get("/api/refreshtoken")
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.header("X-XSRF-TOKEN", securityService.getXsrf())
				.header("Authorization", "Bearer " + securityService.getJwt()))
		.andExpect(status().isOk()).andDo(securityService.getResultHandler());

		assertNotEquals(oldJwt, securityService.getJwt());

	}


}
