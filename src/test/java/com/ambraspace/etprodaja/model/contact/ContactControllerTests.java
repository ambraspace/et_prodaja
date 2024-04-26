package com.ambraspace.etprodaja.model.contact;

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
public class ContactControllerTests
{

	@Autowired
	private CompanyControllerTestComponent companyTestComponent;

	@Autowired
	private ContactControllerTestComponent contactTestComponent;

	@Autowired
	private SecurityTestComponent securityTestComponent;


	@Test
	public void testContactOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");


		Company company = companyTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City and Country"
}
				""");

		Contact contact = contactTestComponent.addContact(company.getId(), """
{
	"name":"Test contact",
	"phone":"+454 (565) 121-111",
	"email":"test@test.com",
	"comment":"This is a test"
}
				""");

		contact = contactTestComponent.getContact(company.getId(), contact.getId());

		assertNotEquals(contact, null);

		assertNotEquals(contactTestComponent.getContacts(company.getId()).size(), 0);

		contact = contactTestComponent.updateContact(company.getId(), contact.getId(), """
{
	"name":"Updated Test contact",
	"phone":"+454 (565) 121-222",
	"email":"test2@test.com",
	"comment":"This is another test"
}
				""");

		assertEquals(contact.getName(), "Updated Test contact");

		contactTestComponent.deleteContact(company.getId(), contact.getId());

		contact = contactTestComponent.getContact(company.getId(), contact.getId());

		assertEquals(contact, null);

		companyTestComponent.deleteCompany(company.getId());

	}

}
