package com.ambraspace.etprodaja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ambraspace.etprodaja", "com.ambraspace.auth.jwt"})
public class EtProdajaApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(EtProdajaApplication.class, args);
	}


    @Bean
    PasswordEncoder passwordEncoder()
    {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    //https://stackoverflow.com/a/71416675/13381019
    @Bean
    Hibernate6Module hibernateModule() {
        return new Hibernate6Module();
    }

}
