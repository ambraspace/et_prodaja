package com.ambraspace.etprodaja.model.delivery;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.company.Company;
import com.ambraspace.etprodaja.model.item.Item;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
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
			@NamedAttributeNode(value = "items", subgraph = "delivery.items")
	}, subgraphs = {
			@NamedSubgraph(name = "delivery.items", attributeNodes = {
					@NamedAttributeNode("stockInfo")
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Company supplier;

	@NotNull @NotBlank
	private String supplierReference;

	private String comment;

	@NotNull
	private LocalDate deliveryDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "delivery")
	private List<Item> items = new ArrayList<Item>();


	public BigDecimal getValue()
	{

		BigDecimal retVal = BigDecimal.ZERO;

		if (items == null || items.size() == 0)
			return retVal;

		items.forEach(i ->
			retVal.add(
				i.getStockInfo().getUnitPrice()
				.multiply(
						i.getQuantity()
				)
				.setScale(2, RoundingMode.HALF_EVEN)
			)
		);

		return retVal;

	}


	void copyFieldsFrom(Delivery other)
	{
		this.setComment(other.getComment());
		this.setDeliveryDate(other.getDeliveryDate());
		this.setId(null);
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
