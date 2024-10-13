package com.ambraspace.etprodaja.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@NamedEntityGraphs({
	@NamedEntityGraph(name = "order-with-details", attributeNodes = {
			@NamedAttributeNode(value = "warehouse", subgraph = "order.warehouse"),
			@NamedAttributeNode(value = "items", subgraph = "order.items")
	}, subgraphs = {
			@NamedSubgraph(name = "order.warehouse", attributeNodes = {
					@NamedAttributeNode("company")
			}),
			@NamedSubgraph(name = "order.items", attributeNodes = {
					@NamedAttributeNode("stockInfo")
			})
	})
})
@Table(name = "`order`")
public class Order
{

	public static enum Status
	{
		OPEN,
		CLOSED
	}

	@Id
	@OrderNoSequence(name = "order_no_seq")
	private String id;  // "N-" + year + sequence no. (N-2023-001)

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Warehouse warehouse;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status;

	@NotNull
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate creationDate = null;

	private LocalDateTime closureTime = null;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
	private List<Item> items = new ArrayList<Item>();

	@Transient
	@JsonProperty
	private BigDecimal value;


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Order other = (Order) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
