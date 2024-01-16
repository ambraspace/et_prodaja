package com.ambraspace.etprodaja.model.offer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.contact.Contact;
import com.ambraspace.etprodaja.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Offer
{

	public enum Status
	{
		ACTIVE,
		ACCEPTED,
		DELIVERED,
		INVOICED,
		CANCELED
	}

	@Id
	@OfferNoSequence(name = "offer_no_seq")
	private String offerNo; // "P-" + year + sequence no. (P-2023-01)

	@ManyToOne(optional = false)
	private User user; // who created the offer

	private LocalDate offerDate;

	private LocalDate validUntil;

	@ManyToOne(optional = false)
	private Company company; // customer

	@ManyToOne
	private Contact contact; // customer's contact

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "offer", orphanRemoval = true)
	private List<Item> items;

	private BigDecimal vat;

	private String notes;

	private String comments;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Offer other = (Offer) o;
		return getOfferNo() != null && Objects.equals(getOfferNo(), other.getOfferNo());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
