package com.ambraspace.etprodaja.model.offer;

import java.math.BigDecimal;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Offer offer;

	@ManyToOne(optional = false)
	private Product product;

	@ManyToOne(optional = false)
	private Warehouse warehouse;

	private BigDecimal quantity = BigDecimal.valueOf(0, 2);

	private BigDecimal grossPrice = BigDecimal.valueOf(0, 2);

	private BigDecimal discountPercent = BigDecimal.valueOf(0, 2);

	private BigDecimal netPrice = BigDecimal.valueOf(0, 2);

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
