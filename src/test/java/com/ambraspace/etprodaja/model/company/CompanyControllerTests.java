package com.ambraspace.etprodaja.model.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ambraspace.etprodaja.SecurityTestComponent;
import com.ambraspace.etprodaja.model.contact.Contact;
import com.ambraspace.etprodaja.model.contact.ContactControllerTestComponent;


@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTests
{

	@Autowired
	private CompanyControllerTestComponent companyTestComponent;

	@Autowired
	private ContactControllerTestComponent contactTestComponent;

	@Autowired
	private SecurityTestComponent securityTestComponent;


	@Test
	public void testCompanyOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		Company company = companyTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City and Country"
}
				""");

		company = companyTestComponent.getCompany(company.getId());

		assertNotEquals(company, null);

		assertNotEquals(companyTestComponent.getCompanies().size(), 0);

		company = companyTestComponent.updateCompany(company.getId(), """
{
	"name":"Updated test company",
	"locality":"City and Country"
}
				""");

		assertTrue(company.getName().equals("Updated test company"));

		Contact contact = contactTestComponent.addContact(company.getId(), """
{
	"name":"Test contact",
	"phone":"+454 (565) 121-111",
	"email":"test@test.com",
	"comment":"This is a test"
}
				""");

		Long id = company.getId();

		// Cannot delete due to referential integrity
		assertThrows(AssertionError.class, () -> {
			companyTestComponent.deleteCompany(id);
		});

		contactTestComponent.deleteContact(id, contact.getId());

		companyTestComponent.deleteCompany(id);

		company = companyTestComponent.getCompany(company.getId());

		assertEquals(company, null);

	}

}
