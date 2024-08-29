package com.ambraspace.etprodaja.model.item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItem;
import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.order.Order;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;
import com.ambraspace.etprodaja.util.Views;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@NamedEntityGraphs(value = {
		@NamedEntityGraph(name = "items", attributeNodes = {
				@NamedAttributeNode(value = "stockInfo", subgraph = "item.stockInfo")
		}, subgraphs = {
				@NamedSubgraph(name = "item.stockInfo", attributeNodes = {
						@NamedAttributeNode("product")
				})
		}),
		@NamedEntityGraph(name = "offer-items", attributeNodes = {
				@NamedAttributeNode("order"),
				@NamedAttributeNode(value = "deliveryItems", subgraph = "item.deliveryItems"),
				@NamedAttributeNode(value = "stockInfo", subgraph = "item.stockInfo")
		}, subgraphs = {
				@NamedSubgraph(name = "item.deliveryItems", attributeNodes = {
						@NamedAttributeNode("delivery")
				}),
				@NamedSubgraph(name = "item.stockInfo", attributeNodes = {
						@NamedAttributeNode("product")
				})
		}),
		@NamedEntityGraph(name = "order-items", attributeNodes = {
				@NamedAttributeNode("offer"),
				@NamedAttributeNode(value = "deliveryItems", subgraph = "item.deliveryItems"),
				@NamedAttributeNode(value = "stockInfo", subgraph = "item.stockInfo")
		}, subgraphs = {
				@NamedSubgraph(name = "item.deliveryItems", attributeNodes = {
						@NamedAttributeNode("delivery")
				}),
				@NamedSubgraph(name = "item.stockInfo", attributeNodes = {
						@NamedAttributeNode("product")
				})
		})
})
public class Item
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Offer offer;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item", orphanRemoval = true)
	@JsonView(value = {Views.Item.class})
	private List<DeliveryItem> deliveryItems = new ArrayList<DeliveryItem>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StockInfo stockInfo;

	@NotNull @NotBlank
	private String productName;

	private String preview;

	@NotNull @PositiveOrZero
	private BigDecimal quantity = BigDecimal.valueOf(0, 2);

	@NotNull @PositiveOrZero
	private BigDecimal grossPrice = BigDecimal.valueOf(0, 2);

	@NotNull @PositiveOrZero
	private BigDecimal discountPercent = BigDecimal.valueOf(0, 2);

	@JsonProperty
	@Transient
	private BigDecimal outstandingQuantity = BigDecimal.ZERO;

	@JsonProperty(access = Access.READ_ONLY)
	public BigDecimal getNetPrice()
	{
		return grossPrice.multiply(
				BigDecimal.ONE.subtract(discountPercent.movePointLeft(2)))
				.setScale(2, RoundingMode.HALF_EVEN);
	}


	public void copyFieldsFrom(Item other)
	{

		this.setDiscountPercent(other.getDiscountPercent());
		this.setGrossPrice(other.getGrossPrice());
		this.setProductName(other.getProductName());
		this.setPreview(other.getPreview());
		this.setQuantity(other.getQuantity());
		this.setStockInfo(other.getStockInfo());

	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Item other = (Item) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
