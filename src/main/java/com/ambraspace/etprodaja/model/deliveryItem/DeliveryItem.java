package com.ambraspace.etprodaja.model.deliveryItem;

import java.math.BigDecimal;

import com.ambraspace.etprodaja.model.delivery.Delivery;
import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.util.Views;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@NamedEntityGraph(
		name = "deliveryItem-with-details",
		attributeNodes = {
				@NamedAttributeNode(value = "item", subgraph = "deliveryItem.item")
		},
		subgraphs = {
				@NamedSubgraph(name ="deliveryItem.item", attributeNodes = {
						@NamedAttributeNode(value = "offer"),
						@NamedAttributeNode(value = "order"),
						@NamedAttributeNode(value = "stockInfo", subgraph = "deliveryItem.item.stockInfo")
				}),
				@NamedSubgraph(name ="deliveryItem.item.stockInfo", attributeNodes = {
						@NamedAttributeNode(value = "product")
				})
		}
)
public class DeliveryItem
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Delivery delivery;

	@JsonView(Views.DeliveryItem.class)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Item item;

	private BigDecimal quantity;

	private String deliveryNote;


	void copyFieldsFrom(DeliveryItem original)
	{
		this.setDeliveryNote(original.getDeliveryNote());
		this.setQuantity(original.getQuantity());
	}

}
