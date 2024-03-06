package com.ambraspace.etprodaja.model.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.user.User.Role;


public interface UserRepository extends CrudRepository<User, String>, PagingAndSortingRepository<User, String>
{

	@Override
	@EntityGraph("user-with-company")
	Optional<User> findById(String id);


	@Override
	@EntityGraph("user-with-company")
	Page<User> findAll(Pageable pageable);


	long countByRole(Role role);

}
