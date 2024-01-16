package com.ambraspace.etprodaja.model.delivery;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Delivery
{

	public enum Status
	{
		EXPECTING,
		DELIVERED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String supplierReference;

	private String comment;

	private LocalDate deliveryTime;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "delivery", orphanRemoval = true)
	List<DeliveryNote> deliverItems;


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
