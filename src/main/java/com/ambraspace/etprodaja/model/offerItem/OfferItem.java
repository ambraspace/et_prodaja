package com.ambraspace.etprodaja.model.offerItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.offer.Offer.Status;
import com.ambraspace.etprodaja.model.orderItem.OrderItem;
import com.ambraspace.etprodaja.model.product.Product;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@NamedEntityGraphs(value = {
		@NamedEntityGraph(name = "offer-items", attributeNodes = {
				@NamedAttributeNode(value = "orderItems", subgraph = "offerItem.orderItems"),
				@NamedAttributeNode("product")
		}, subgraphs = {
				@NamedSubgraph(name = "offerItem.orderItems", attributeNodes = {
						@NamedAttributeNode("order"),
						@NamedAttributeNode(value = "stockInfo", subgraph = "offerItem.orderItems.stockInfo"),
						@NamedAttributeNode(value = "deliveryItems", subgraph = "offerItem.orderItems.deliveryItems")
				}),
				@NamedSubgraph(name = "offerItem.orderItems.stockInfo", attributeNodes = {
						@NamedAttributeNode("warehouse")
				}),
				@NamedSubgraph(name = "offerItem.orderItems.deliveryItems", attributeNodes = {
						@NamedAttributeNode("delivery")
				})
		})
})
public class OfferItem
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Offer offer;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "offerItem")
	@JsonView(value = {Views.OfferItem.class, Views.DeliveryItem.class})
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Product product;

	@NotNull @NotBlank
	private String productDescription;

	private String preview;

	@NotNull @PositiveOrZero
	private BigDecimal quantity = BigDecimal.valueOf(0, 2);

	@NotNull @PositiveOrZero
	private BigDecimal grossPrice = BigDecimal.valueOf(0, 2);

	@NotNull @PositiveOrZero
	private BigDecimal discountPercent = BigDecimal.valueOf(0, 2);

	@JsonProperty(access = Access.READ_ONLY)
	public BigDecimal getNetPrice()
	{
		return grossPrice.multiply(
				BigDecimal.ONE.subtract(discountPercent.movePointLeft(2)))
				.setScale(2, RoundingMode.HALF_EVEN);
	}


	public void copyFieldsFrom(OfferItem other)
	{

		if (
				(this.offer.getStatus() != Status.ACTIVE ||
				(this.orderItems != null && this.orderItems.size() > 0 )) &&
				this.product.getId() != other.getProduct().getId()
		)
			throw new RuntimeException("Can not change the product if it's already ordered!");

		this.setProduct(other.getProduct());
		this.setProductDescription(other.getProductDescription());
		this.setPreview(other.getPreview());
		this.setQuantity(other.getQuantity());
		this.setGrossPrice(other.getGrossPrice());
		this.setDiscountPercent(other.getDiscountPercent());

	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		OfferItem other = (OfferItem) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
