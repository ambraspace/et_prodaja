package com.ambraspace.etprodaja.model.warehouse;

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
public class WarehouseControllerTests
{

	@Autowired
	private CompanyControllerTestComponent companyControllerTestComponent;

	@Autowired
	private WarehouseControllerTestComponent warehouseControllerTestComponent;

	@Autowired
	private SecurityTestComponent securityTestComponent;


	@Test
	public void testWarehouseOperations() throws Exception
	{

		securityTestComponent.authenticate("admin", "administrator");

		Company company = companyControllerTestComponent.addCompany("""
{
	"name":"Test company",
	"locality":"City and country"
}
				""");

		Warehouse warehouse = warehouseControllerTestComponent.addWarehouse(
				company.getId(),
				"""
{
	"name":"Test warehouse"
}
				""");

		assertNotEquals(warehouse, null);

		warehouse = warehouseControllerTestComponent.getWarehouse(company.getId(), warehouse.getId());

		assertNotEquals(warehouse, null);

		assertEquals(warehouse.getName(), "Test warehouse");

		warehouse = warehouseControllerTestComponent.updateWarehouse(
				company.getId(),
				warehouse.getId(),
				"""
{
	"name":"Updated test warehouse"
}
				""");

		assertNotEquals(warehouse, null);

		assertEquals(warehouse.getName(), "Updated test warehouse");

		assertNotEquals(warehouseControllerTestComponent.getWarehouses(company.getId()).size(), 0);

		warehouseControllerTestComponent.deleteWarehouse(company.getId(), warehouse.getId());

		warehouse = warehouseControllerTestComponent.getWarehouse(company.getId(), warehouse.getId());

		assertEquals(warehouse, null);

		companyControllerTestComponent.deleteCompany(company.getId());

	}

}
