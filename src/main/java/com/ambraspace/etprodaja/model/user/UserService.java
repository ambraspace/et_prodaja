package com.ambraspace.etprodaja.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.company.CompanyService;
import com.ambraspace.etprodaja.model.user.User.Role;

import jakarta.annotation.PostConstruct;

@Service
public class UserService implements UserDetailsService
{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CompanyService companyService;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{

		User user = userRepository.findById(username).orElseThrow();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        return userDetails;

	}


	public User getUser(String username)
	{
		return userRepository.findById(username).orElse(null);
	}


	public Page<User> getUsers(Pageable pageable)
	{
		return userRepository.findAll(pageable);
	}


	public User addUser(User user)
	{

		User fromRep = getUser(user.getUsername());

		if (fromRep != null)
			throw new RuntimeException("Username already exists!");

		if (user.getPassword() == null || user.getPassword().trim().equals(""))
			throw new RuntimeException("Please set user password!");

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);

	}


	@Transactional
	public User updateUser(String username, User user)
	{

		User fromRep = getUser(username);

		if (fromRep == null)
			throw new RuntimeException("No such user in the database!");

		// If we're changing ADMIN to non-ADMIN
		if (fromRep.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.ADMIN))
		{
			long numAdmins = userRepository.countByRole(Role.ADMIN);
			if (numAdmins == 1)
				throw new RuntimeException("Requested operation would result with no ADMIN users!");
		}

		fromRep.copyFieldsFrom(user);

		if (user.getPassword() != null && !user.getPassword().trim().equals(""))
		{
			fromRep.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		return userRepository.save(fromRep);

	}


	@Transactional
	public void deleteUser(String username)
	{

		User fromRep = getUser(username);

		if (fromRep == null)
			throw new RuntimeException("No such user in the database!");

		long numOfAdmins = userRepository.countByRole(User.Role.ADMIN);

		if (numOfAdmins == 1)
			throw new RuntimeException("Cannot delete the last ADMIN user!");


		userRepository.deleteById(username);

	}

	@PostConstruct
	public void init()
	{

		long numOfAdmins = userRepository.countByRole(User.Role.ADMIN);

		if (numOfAdmins == 0)
		{

			Company company = new Company();
			company.setLocality("Home");
			company.setName("Default company");
			companyService.addCompany(company);

			User adam = new User();
			adam.setCanViewPrices(true);
			adam.setCompany(company);
			adam.setEmail("my@default.email");
			adam.setFullName("Administrator");
			adam.setPassword("administrator");
			adam.setPhone("123-456");
			adam.setRole(Role.ADMIN);
			adam.setSignature("Admin");
			adam.setUsername("admin");
			addUser(adam);

		}

	}

}
