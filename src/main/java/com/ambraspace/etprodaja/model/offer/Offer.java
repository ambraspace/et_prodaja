package com.ambraspace.etprodaja.model.offer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.contact.Contact;
import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.user.User;
import com.ambraspace.etprodaja.util.LocalDateDeserializer;
import com.ambraspace.etprodaja.util.LocalDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@NamedEntityGraphs({
		@NamedEntityGraph(name = "offer-with-details", attributeNodes = {
				@NamedAttributeNode("user"),
				@NamedAttributeNode("company"),
				@NamedAttributeNode("contact"),
				@NamedAttributeNode(value = "items", subgraph = "offer.items")
		}, subgraphs = {
				@NamedSubgraph(name = "offer.items", attributeNodes = {
						@NamedAttributeNode("stockInfo")
				})
		})
})
public class Offer
{

	/*
	 * After adding or deleting statuses SQL queries in repositories must be double checked!
	 */
	public static enum Status
	{
		ACTIVE,
		ACCEPTED,
		CANCELED
	}

	@Id
	@OfferNoSequence(name = "offer_no_seq")
	private String id; // "P-" + year + sequence no. (P-2023-001)

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private User user; // who created the offer

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate offerDate;

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate validUntil;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Company company; // customer

	@ManyToOne(fetch = FetchType.LAZY)
	private Contact contact; // customer's contact

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "offer", orphanRemoval = true)
	private List<Item> items = new ArrayList<Item>();

	@NotNull @PositiveOrZero
	private BigDecimal vat;

	@Column(length = 500)
	private String notes;

	@Column(length = 500)
	private String comments;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

	@Transient
	@JsonProperty
	private BigDecimal value;

	@Transient
	@JsonProperty
	private BigDecimal cost;

	@Transient
	@JsonProperty
	private BigDecimal margin;


	public void copyFieldsFrom(Offer other)
	{

		this.setComments(other.getComments());
		this.setCompany(other.getCompany());
		this.setContact(other.getContact());
		this.setNotes(other.getNotes());
		this.setValidUntil(other.getValidUntil());
		this.setVat(other.getVat());

	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Offer other = (Offer) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
