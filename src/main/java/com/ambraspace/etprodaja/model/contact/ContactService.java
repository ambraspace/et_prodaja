package com.ambraspace.etprodaja.model.contact;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambraspace.etprodaja.model.company.Company;

@Service
public class ContactService
{

	@Autowired
	private ContactRepository contactRepository;


	public List<Contact> getContacts(Long companyId)
	{
		List<Contact> retVal = new ArrayList<Contact>();
		contactRepository.findByCompanyId(companyId).forEach(retVal::add);
		return retVal;
	}


	public Contact getContact(Long companyId, Long id)
	{
		return contactRepository.findByCompanyIdAndId(companyId, id).orElse(null);
	}


	public Contact addContact(Long companyId, Contact contact)
	{

		Company c = new Company();
		c.setId(companyId);

		contact.setCompany(c);

		return contactRepository.save(contact);

	}


	public Contact updateContact(Long companyId, Long id, Contact contact)
	{

		Contact fromRep = getContact(companyId, id);

		if (fromRep == null)
			throw new RuntimeException("No such contact in the database!");

		fromRep.copyFieldsFrom(contact);

		return contactRepository.save(fromRep);

	}


	public void deleteContact(Long companyId, Long id)
	{

		Contact fromRep = getContact(companyId, id);

		if (fromRep == null)
			throw new RuntimeException("No such contact in the database!");

		contactRepository.delete(fromRep);

	}

}
