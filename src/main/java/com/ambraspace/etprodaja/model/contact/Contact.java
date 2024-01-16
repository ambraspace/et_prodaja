package com.ambraspace.etprodaja.model.contact;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Contact
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Company company;

	@NotNull @NotBlank @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters long!")
	private String name;

	@Pattern(regexp = "[\\d-()/+ ]*", message = "Contact's phone number not properly formed")
	@Size(min = 3, max = 30, message = "Contact's phone number must be between 3 and 30 characters long")
	private String phone;

	@Email(message = "Contact's email address not properly formed")
	private String email;

	@Size(max = 255, message = "Comment must not exceed 255 characters")
	private String comment;


	void copyFieldsFrom(Contact other)
	{
		this.name = other.getName();
		this.phone = other.getPhone();
		this.email = other.getEmail();
		this.comment = other.getComment();
	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Contact other = (Contact) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
