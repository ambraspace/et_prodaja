package com.ambraspace.etprodaja.model.item;

import java.math.BigDecimal;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.delivery.Delivery;
import com.ambraspace.etprodaja.model.offer.Offer;
import com.ambraspace.etprodaja.model.order.Order;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Item
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Offer offer;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	private Delivery delivery;

	private String deliveryNote;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StockInfo stockInfo;

	@NotNull @PositiveOrZero
	private BigDecimal quantity = BigDecimal.valueOf(0, 2);

	@NotNull @PositiveOrZero
	private BigDecimal grossPrice = BigDecimal.valueOf(0, 2);

	@NotNull @PositiveOrZero
	private BigDecimal discountPercent = BigDecimal.valueOf(0, 2);


	public BigDecimal getNetPrice()
	{
		return grossPrice.multiply(
				BigDecimal.ONE.subtract(discountPercent.movePointLeft(2)))
				.setScale(2);
	}


	public void copyFieldsFrom(Item other)
	{

		this.setDelivery(other.getDelivery());
		this.setDeliveryNote(other.getDeliveryNote());
		this.setDiscountPercent(other.getDiscountPercent());
		this.setGrossPrice(other.getGrossPrice());
		this.setOrder(other.getOrder());
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
