package com.ambraspace.etprodaja.model.contact;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends CrudRepository<Contact, Long>, PagingAndSortingRepository<Contact, Long>
{

	Iterable<Contact> findByCompanyId(Long companyId);

	Optional<Contact> findByCompanyIdAndId(Long companyId, Long id);

}
