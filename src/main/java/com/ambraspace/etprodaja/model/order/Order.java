package com.ambraspace.etprodaja.model.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;

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
import jakarta.persistence.Table;
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
					@NamedAttributeNode("offer"),
					@NamedAttributeNode("delivery"),
					@NamedAttributeNode(value = "stockInfo", subgraph = "order.items.stockInfo")
			}),
			@NamedSubgraph(name = "order.items.stockInfo", attributeNodes = {
					@NamedAttributeNode("product")
			})
	})
})
@Table(name = "`order`")
public class Order
{

	public enum Status
	{
		OPEN,
		CLOSED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Warehouse warehouse;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status;

	private LocalDateTime closureTime = null;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true)
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
