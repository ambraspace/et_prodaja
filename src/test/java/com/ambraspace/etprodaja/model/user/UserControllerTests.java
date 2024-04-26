package com.ambraspace.etprodaja.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.company.CompanyControllerTestComponent;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

	@Autowired
	private SecurityTestComponent securityTestComponent;

	@Autowired
	private UserControllerTestComponent userControllerTestComponent;

	@Autowired
	private CompanyControllerTestComponent companyControllerTestComponent;


	@Test
	public void testUserOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		Company company = companyControllerTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City and Country"
}
				""");


		User user = userControllerTestComponent.addUser(String.format("""
{
	"username":"dummy",
	"password":"min_10_characters",
	"role":"USER",
	"fullName":"Dummy User",
	"company":{"id":%d},
	"phone":"+35 44 786-444",
	"email":"test@test.com",
	"signature":"Dummy User, sales manager",
	"canViewPrices":true
}
				""", company.getId()));

		user = userControllerTestComponent.getUser(user.getUsername());

		assertEquals(user.getPassword(), null);

		user = userControllerTestComponent.getUser(user.getUsername());

		assertNotEquals(user, null);

		assertEquals(user.getPassword(), null);

		Company company2 = companyControllerTestComponent.addCompany("""
{
	"name":"Second test company",
	"locality":"City and Country"
}
				""");

		user = userControllerTestComponent.updateUser(user.getUsername(), String.format("""
{
	"username":"dummy2",
	"password":"min_10_characters2",
	"role":"ADMIN",
	"fullName":"Dummy User 2",
	"company":{"id":%d},
	"phone":"+35 44 786-445",
	"email":"test@test2.com",
	"signature":"Second dummy User, sales manager",
	"canViewPrices":false
}
				""", company2.getId()));

		assertNotEquals(user, null);

		assertEquals(user.getUsername(), "dummy");

		assertEquals(user.getCompany().getId(), company2.getId());

		assertEquals(user.getRole(), User.Role.ADMIN);

		assertNotEquals(userControllerTestComponent.getUsers().size(), 0);

		userControllerTestComponent.deleteUser(user.getUsername());

		assertEquals(userControllerTestComponent.getUser(user.getUsername()), null);

		companyControllerTestComponent.deleteCompany(company2.getId());

		companyControllerTestComponent.deleteCompany(company.getId());

	}



}
