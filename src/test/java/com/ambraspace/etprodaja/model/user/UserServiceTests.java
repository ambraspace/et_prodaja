package com.ambraspace.etprodaja.model.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.company.CompanyService;
import com.ambraspace.etprodaja.model.user.User.Role;

@SpringBootTest
public class UserServiceTests
{

	@Autowired
	private UserService userService;

	@Autowired
	private CompanyService companyService;


	@Test
	public void testUserAddition()
	{

		Company company = companyService.getCompanies(Pageable.unpaged()).stream().findFirst().orElse(null);

		if (company == null)
		{
			company = new Company();
			company.setLocality("My locality");
			company.setName("My company");
			company = companyService.addCompany(company);
		}

		User user = new User();
		user.setCanViewPrices(true);
		user.setCompany(company);
		user.setEmail("fake@email.com");
		user.setFullName("Full name must be long");
		user.setPassword(null);
		user.setPhone("123-456");
		user.setRole(Role.USER);
		user.setSignature("some signature");
		user.setUsername("username");

		assertThrows(RuntimeException.class, () -> {
			userService.addUser(user);
		});

		user.setPassword("     ");

		assertThrows(RuntimeException.class, () -> {
			userService.addUser(user);
		});

		user.setPassword("123546789123");

		assertDoesNotThrow(() -> {
			userService.addUser(user);
		});

		assertDoesNotThrow(() -> {
			userService.deleteUser(user.getUsername());
		});


	}


	@Test
	public void testUserUpdate()
	{

		Page<Company> companies = companyService.getCompanies(Pageable.unpaged());

		Company c1, c2;

		if (companies.getSize() == 0)
		{

			c1 = new Company(null, "Company 1", "Locality 1");
			assertDoesNotThrow(() -> {
				companyService.addCompany(c1);
			});
			c2 = new Company(null, "Company 2", "Locality 2");
			assertDoesNotThrow(() -> {
				companyService.addCompany(c2);
			});
		} else if (companies.getSize() == 1) {
			c1 = companies.getContent().get(0);
			c2 = new Company(null, "Company 2", "Locality 2");
			assertDoesNotThrow(() -> {
				companyService.addCompany(c2);
			});
		} else {
			c1 = companies.getContent().get(0);
			c2 = companies.getContent().get(1);
		}

		User user = new User();

		user.setCanViewPrices(true);
		user.setCompany(c1);
		user.setEmail("fake@email.com");
		user.setFullName("Full name must be long");
		user.setPassword("123546789123");
		user.setPhone("123-456");
		user.setRole(Role.USER);
		user.setSignature("some signature");
		user.setUsername("username");

		assertDoesNotThrow(() -> {
			userService.addUser(user);
		});

		user.setCanViewPrices(false);
		user.setCompany(new Company(c2.getId(), null, null));
		user.setEmail("fake2@email.com");
		user.setFullName("Full name must be long too");
		user.setPassword("1235467891231");
		user.setPhone("123-4561");
		user.setRole(Role.ADMIN);
		user.setSignature("some signature too");

		assertDoesNotThrow(() -> {
			userService.updateUser(user.getUsername(), user);
		});

		assertDoesNotThrow(() -> {
			userService.deleteUser(user.getUsername());
		});

	}

}
