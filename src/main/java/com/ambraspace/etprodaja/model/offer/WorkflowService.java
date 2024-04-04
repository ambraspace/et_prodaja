package com.ambraspace.etprodaja.model.offer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.order.OrderService;
import com.ambraspace.etprodaja.model.user.User;
import com.ambraspace.etprodaja.model.user.UserService;

@Service
public class WorkflowService
{

	@Autowired
	private OfferService offerService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;


	@Transactional
	public Offer cancelOffer(String offerId, String reason)
	{
		return offerService.cancelOffer(offerId, reason);
	}


	@Transactional
	public Offer acceptOffer(String offerId)
	{

		Offer offer = offerService.acceptOffer(offerId);

		orderService.orderItems(offer.getItems());

		return offer;

	}


	@Transactional
	public Offer duplicateOffer(String offerNo, String username)
	{

		User user = userService.getUser(username);

		if (user == null)
			throw new RuntimeException("User not specified!");

		Offer fromRep = offerService.getOffer(offerNo);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		Offer duplicatedOffer = offerService.duplicateOffer(fromRep, user);

		List<Item> duplicatedItems = itemService.duplicateItems(fromRep.getItems(), duplicatedOffer);

		duplicatedOffer.getItems().addAll(duplicatedItems);

		return duplicatedOffer;

	}



}
