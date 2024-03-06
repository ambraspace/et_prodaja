package com.ambraspace.etprodaja.model.company;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Company
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull @NotBlank @Size(min = 2, max = 50, message = "Company mame must be between 1 and 50 characters long")
	@Column(nullable = false)
	private String name;

	@NotNull @NotBlank @Size(min = 2, max = 50, message = "Company locality must be between 1 and 50 characters long")
	@Column(nullable = false)
	private String locality;

//	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
//	private List<Contact> contacts = new ArrayList<Contact>();
//
//	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
//	private List<Warehouse> warehouses = new ArrayList<Warehouse>();


	/**
	 * This method is used for DB update operation.
	 * It updates all fields to new values, except ID.
	 * @param company
	 */
	void copyFieldsFrom(Company company)
	{

		this.setName(company.getName());

		this.setLocality(company.getLocality());

	}

	/*
	 * https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
	 */
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Company other = (Company) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
