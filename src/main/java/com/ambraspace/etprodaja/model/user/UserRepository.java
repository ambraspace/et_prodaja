package com.ambraspace.etprodaja.model.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambraspace.etprodaja.model.user.User.Role;


public interface UserRepository extends CrudRepository<User, String>, PagingAndSortingRepository<User, String>
{

	long countByRole(Role role);

}
