package com.ambraspace.etprodaja.model.stockinfo;

import java.math.BigDecimal;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.product.Product;
import com.ambraspace.etprodaja.model.warehouse.Warehouse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_id", "product_id"}))
public class StockInfo
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(optional = false)
	private Warehouse warehouse;

	@JsonIgnore
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Product product;

	private String customerReference;

	@NotNull @PositiveOrZero
	private BigDecimal quantity = BigDecimal.ZERO;

	@NotNull @PositiveOrZero
	private BigDecimal unitPrice = BigDecimal.ZERO;


	void copyFieldsFrom(StockInfo other)
	{

		this.customerReference = other.getCustomerReference();
		this.quantity = other.getQuantity();
		this.unitPrice = other.getUnitPrice();

	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		StockInfo other = (StockInfo) o;
		return getWarehouse() != null && getProduct() != null
				&& Objects.equals(getWarehouse(), other.getWarehouse())
				&& Objects.equals(getProduct(), other.getProduct());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
