package com.ambraspace.etprodaja.model.orderItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.deliveryItem.DeliveryItem;
import com.ambraspace.etprodaja.model.offerItem.OfferItem;
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
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class OrderItem
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JsonView(value = {Views.OrderItem.class, Views.DeliveryItem.class})
	private OfferItem offerItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StockInfo stockInfo;

	private BigDecimal quantity;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "orderItem")
	@JsonView(value = {Views.OfferItem.class, Views.OrderItem.class})
	private List<DeliveryItem> deliveryItems = new ArrayList<DeliveryItem>();


	@JsonProperty(access = Access.READ_ONLY)
	public BigDecimal getOutstandingQuantity()
	{

		if (deliveryItems == null || deliveryItems.size() == 0)
			return quantity;

		BigDecimal retVal = quantity;

		for (DeliveryItem di:deliveryItems)
		{
			retVal = retVal.subtract(di.getQuantity());
		}

		return retVal;

	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		OrderItem other = (OrderItem) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}


}
