package com.ambraspace.etprodaja.model.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompanyService
{

	@Autowired
	private CompanyRepository companyRepository;


	public Page<Company> getCompanies(Pageable pageable)
	{
		return companyRepository.findAll(pageable);
	}


	public Company getCompany(Long id)
	{
		return companyRepository.findById(id).orElse(null);
	}


	public Company addCompany(Company company)
	{

		//company.getContacts().forEach(c -> c.setCompany(company));

		//company.getWarehouses().forEach(w -> w.setCompany(company));

		return companyRepository.save(company);

	}


	public Company updateCompany(Long id, Company company)
	{

		Company fromRep = getCompany(id);

		if (fromRep == null)
			throw new RuntimeException("No such company in the database!");

		fromRep.copyFieldsFrom(company);

		return companyRepository.save(fromRep);

	}


	public void deleteCompany(Long id)
	{

		Company fromRep = getCompany(id);

		if (fromRep == null)
			throw new RuntimeException("No such company in the database!");

		companyRepository.deleteById(id);

	}


}
