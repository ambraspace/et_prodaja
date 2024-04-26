package com.ambraspace.etprodaja.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.ambraspace.etprodaja.model.category.Category;
import com.ambraspace.etprodaja.model.stockinfo.StockInfo;
import com.ambraspace.etprodaja.model.tag.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(name = "product-with-previews", attributeNodes = {
			@NamedAttributeNode("previews")
	}),
	@NamedEntityGraph(name = "product-with-category-and-tags", attributeNodes = {
			@NamedAttributeNode("category"),
			@NamedAttributeNode("tags")
	})
})
@Getter @Setter @NoArgsConstructor
public class Product
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull @NotBlank @Size(min = 5, max = 255, message = "Product name must be between 5 and 255 characters long")
	private String name;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@NotNull
	private List<Preview> previews = new ArrayList<Preview>();

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private List<StockInfo> stockInfos = new ArrayList<StockInfo>();

	@NotNull @NotBlank
	private String unit;

	@NotNull @Positive
	private BigDecimal price = BigDecimal.ZERO;

	@Transient
	@JsonProperty
	private BigDecimal purchasePrice = BigDecimal.ZERO;

	@Transient
	@JsonProperty
	private BigDecimal availableQty = BigDecimal.ZERO;

	@Transient
	@JsonProperty
	private BigDecimal offeredQty = BigDecimal.ZERO;

	@Transient
	@JsonProperty
	private BigDecimal orderedQty = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Category category;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<Tag> tags;

	private String comment;


	void copyFieldsFrom(Product other)
	{
		this.name = other.getName();
		this.previews.clear();
		this.previews.addAll(other.getPreviews());
		this.unit = other.getUnit();
		this.price = other.getPrice();
		this.category = other.getCategory();
		this.tags.clear();
		this.tags.addAll(other.getTags());
		this.comment = other.getComment();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Product other = (Product) o;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
						: getClass().hashCode();
	}

}
