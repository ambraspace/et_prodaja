package com.ambraspace.etprodaja.model.user;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class User
{

	public enum Role
	{
		ADMIN,
		USER,
		CUSTOMER
	}

	@Id
	private String username;

	@JsonIgnore
	@JsonDeserialize
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	private String fullName;

	@ManyToOne(optional = false)
	private Company company;

	private String phone;

	private String email;

	private String signature;

	private boolean canViewPrices = true;


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		User other = (User) o;
		return getUsername() != null && Objects.equals(getUsername(), other.getUsername());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
