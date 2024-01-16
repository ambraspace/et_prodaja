package com.ambraspace.etprodaja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EtProdajaApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(EtProdajaApplication.class, args);
	}


	/*
	 * https://github.com/springdoc/springdoc-openapi/issues/833#issuecomment-1067895977
	 */
//	@Bean
//	MappingJackson2HttpMessageConverter octetStreamJsonConverter() {
//	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//	    converter.setSupportedMediaTypes(List.of(new MediaType("application", "octet-stream")));
//	    return converter;
//	}

}
