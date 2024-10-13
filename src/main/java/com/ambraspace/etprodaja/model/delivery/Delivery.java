package com.ambraspace.etprodaja.model.delivery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItem;
import com.ambraspace.etprodaja.util.LocalDateDeserializer;
import com.ambraspace.etprodaja.util.LocalDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@NamedEntityGraphs({
	@NamedEntityGraph(name = "delivery-with-details", attributeNodes = {
			@NamedAttributeNode("supplier"),
			@NamedAttributeNode(value = "deliveryItems", subgraph = "delivery.deliveryItems")
	}, subgraphs = {
			@NamedSubgraph(name = "delivery.deliveryItems", attributeNodes = {
					@NamedAttributeNode(value = "item", subgraph = "delivery.deliveryItems.item")
			}),
			@NamedSubgraph(name = "delivery.deliveryItems.item", attributeNodes = {
					@NamedAttributeNode(value = "stockInfo")
			})
	})
})
public class Delivery
{

	public static enum Status
	{
		ON_THE_WAY,
		DELIVERED
	}

	@Id
	@DeliveryNoSequence(name = "delivery_no_seq")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Company supplier;

	@NotNull @NotBlank
	private String supplierReference;

	private String comment;

	@NotNull
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate deliveryDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "delivery", orphanRemoval = true)
	private List<DeliveryItem> deliveryItems = new ArrayList<DeliveryItem>();

	@Transient
	@JsonProperty
	private BigDecimal value;


	void copyFieldsFrom(Delivery other)
	{
		this.setComment(other.getComment());
		this.setDeliveryDate(other.getDeliveryDate());
		this.setSupplier(other.getSupplier());
		this.setSupplierReference(other.getSupplierReference());
	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Delivery other = (Delivery) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
