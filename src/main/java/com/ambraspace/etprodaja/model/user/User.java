package com.ambraspace.etprodaja.model.user;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

	@NotNull @NotBlank @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters long!")
	@Id
	private String username;

	@Size(min = 10, message = "Password must be at least 10 characters long!")
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Role role;

	@NotNull @NotBlank @Size(min = 10, max = 50, message = "Full name must be between 10 and 50 characters long!")
	private String fullName;

	@ManyToOne(optional = false)
	private Company company;

	@Pattern(regexp = "[\\d-()/+ ]*", message = "Phone number must be properly formed")
	@NotNull @NotBlank @Size(min = 3, max = 30, message = "Phone number must be between 6 and 30 characters long")
	private String phone;

	@Email(message = "Email address must be properly formed")
	private String email;

	@NotNull @NotBlank
	private String signature = "Potpis";

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

	public void copyFieldsFrom(User other)
	{

		this.setCanViewPrices(other.isCanViewPrices());
		this.setCompany(other.getCompany());
		this.setEmail(other.getEmail());
		this.setFullName(other.getFullName());
		this.setPassword(other.getPassword());
		this.setPhone(other.getPhone());
		this.setRole(other.getRole());
		this.setSignature(other.getSignature());

	}

}
