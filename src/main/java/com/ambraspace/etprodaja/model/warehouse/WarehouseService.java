package com.ambraspace.etprodaja.model.warehouse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.company.CompanyService;

@Service
public class WarehouseService
{

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private CompanyService companyService;



	public List<Warehouse> getWarehouses(Long companyId)
	{
		List<Warehouse> retVal = new ArrayList<Warehouse>();
		warehouseRepository.findByCompanyIdOrderByName(companyId).forEach(retVal::add);
		return retVal;
	}


	public Warehouse getWarehouse(Long companyId, Long id)
	{
		return warehouseRepository.findByCompanyIdAndId(companyId ,id).orElse(null);
	}


	@Transactional
	public Warehouse addWarehouse(Long companyId, Warehouse warehouse)
	{

		Company company = companyService.getCompany(companyId);

		if (company == null)
			throw new RuntimeException("No such company in the database!");

		warehouse.setCompany(company);

		return warehouseRepository.save(warehouse);

	}


	@Transactional
	public Warehouse updateWarehouse(Long companyId, Long id, Warehouse warehouse)
	{

		Warehouse fromRep = getWarehouse(companyId, id);

		if (fromRep == null)
			throw new RuntimeException("Warehouse not found in the database!");

		fromRep.copyFieldsFrom(warehouse);

		return warehouseRepository.save(fromRep);

	}


	@Transactional
	public void deleteWarehouse(Long companyId, Long id)
	{

		Warehouse warehouse = getWarehouse(companyId, id);

		if (warehouse == null)
			throw new RuntimeException("Warehouse not found!");

		warehouseRepository.deleteById(id);

	}


	public List<Warehouse> searchWarehouses(String query, Integer size)
	{
		List<Warehouse> retVal = new ArrayList<Warehouse>();
		warehouseRepository.searchWarehouse("%" + query + "%", size).forEach(retVal::add);
		return retVal;
	}

}
