package com.ambraspace.etprodaja;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import jakarta.servlet.http.Cookie;


@Component
public class SecurityTestComponent {


	@Autowired
	private MockMvc mockMvc;

	private static final Logger logger = LoggerFactory.getLogger(SecurityTestComponent.class);


	private final ResultHandler resultHandler = new ResultHandler() {

		@Override
		public void handle(MvcResult result) throws Exception {

			if (result.getRequest().getPathInfo().equals("/authenticate") &&
					result.getResponse().getStatus() == HttpStatus.OK.value())
			{
				logger.debug("Setting JWT");
				JSONObject body = new JSONObject(result.getResponse().getContentAsString());
				SecurityTestComponent.this.jwt = body.getString("jwttoken");
				logger.debug("JWT: " + SecurityTestComponent.this.jwt);
			} else if (result.getRequest().getPathInfo().equals("/refreshtoken") &&
					result.getResponse().getStatus() == HttpStatus.OK.value())
			{
				logger.debug("Refreshing JWT");
				JSONObject body = new JSONObject(result.getResponse().getContentAsString());
				SecurityTestComponent.this.jwt = body.getString("jwttoken");
				logger.debug("JWT: " + SecurityTestComponent.this.jwt);
			}

			Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
			if (cookie != null)
			{
				if (cookie.getMaxAge() == 0)
				{
					SecurityTestComponent.this.xsrfValidity = new Date(0);
				} else if (cookie.getMaxAge() == -1)
				{
					SecurityTestComponent.this.xsrfValidity = null;
					SecurityTestComponent.this.xsrf = cookie.getValue();
				}
			}

		}

	};


	private String jwt = null;

	private String xsrf = null;

	private Date xsrfValidity = null;


	public ResultHandler getResultHandler() {
		return resultHandler;
	}


	public void authenticate(String username, String password) throws Exception
	{

		String body = String.format("""
{
  "username":"%s",
  "password":"%s"
}
				""", username, password);

		this.mockMvc.perform(post("/authenticate")
				.with(csrf())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.header("X-XSRF-TOKEN", getXsrf()))
		.andExpect(status().isOk()).andDo(getResultHandler());

	}


	public String getJwt() {
		return jwt;
	}


	public String getXsrf() {

		if (xsrf == null)
		{
			logger.debug("XSRF token is not set. Try to get new one.");
			getNewCsrfToken();
			logger.debug("Returning XSRF token: " + xsrf);
			return xsrf;
		}

		if (xsrfValidity != null && xsrfValidity.compareTo(new Date()) < 1)
		{
			logger.debug("XSRF token is not valid. Try to get new one.");
			getNewCsrfToken();
			logger.debug("Returning XSRF token: " + xsrf);
			return xsrf;
		}

		logger.debug("Returning XSRF token: " + xsrf);
		return xsrf;

	}


	private void getNewCsrfToken()
	{
		try {
			mockMvc.perform(get("/csrf")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(getResultHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
