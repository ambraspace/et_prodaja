package com.ambraspace.etprodaja.model.offer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.item.ItemService;
import com.ambraspace.etprodaja.model.offer.Offer.Status;
import com.ambraspace.etprodaja.model.order.OrderService;
import com.ambraspace.etprodaja.model.user.User;
import com.ambraspace.etprodaja.model.user.UserService;

@Service @Transactional
public class OfferService
{

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;


	public Offer getOffer(String offerId)
	{
		return offerRepository.findById(offerId).orElse(null);
	}


	public Page<Offer> getOffers(String username, Long companyId, Status status, boolean onlyOverdue, Pageable pageable)
	{
		if (username == null)
		{
			if (companyId == null)
			{
				if (status == null)
				{
					if (onlyOverdue)
					{
						return getOffersByOnlyOverdue(pageable);
					} else {
						return getOffers(pageable);
					}
				} else {
					if (onlyOverdue)
					{
						return getOffersByStatusAndOnlyOverdue(status, pageable);
					} else {
						return getOffersByStatus(status, pageable);
					}
				}
			} else {
				if (status == null)
				{
					if (onlyOverdue)
					{
						return getOffersByCompanyIdAndOnlyOverdue(companyId, pageable);
					} else {
						return getOffersByCompanyId(companyId, pageable);
					}
				} else {
					if (onlyOverdue)
					{
						return getOffersByCompanyIdAndStatusAndOnlyOverdue(companyId, status, pageable);
					} else {
						return getOffersByCompanyIdAndStatus(companyId, status, pageable);
					}
				}
			}
		} else {
			if (companyId == null)
			{
				if (status == null)
				{
					if (onlyOverdue)
					{
						return getOffersByUsernameAndOnlyOverdue(username, pageable);
					} else {
						return getOffersByUsername(username, pageable);
					}
				} else {
					if (onlyOverdue)
					{
						return getOffersByUsernameAndStatusAndOnlyOverdue(username, status, pageable);
					} else {
						return getOffersByUsernameAndStatus(username, status, pageable);
					}
				}
			} else {
				if (status == null)
				{
					if (onlyOverdue)
					{
						return getOffersByUsernameAndCompanyIdAndOnlyOverdue(username, companyId, pageable);
					} else {
						return getOffersByUsernameAndCompanyId(username, companyId, pageable);
					}
				} else {
					if (onlyOverdue)
					{
						return getOffersByUsernameAndCompanyIdAndStatusAndOnlyOverdue(username, companyId, status, pageable);
					} else {
						return getOffersByUsernameAndCompanyIdAndStatus(username, companyId, status, pageable);
					}
				}
			}
		}
	}


	private Page<Offer> getOffersByUsernameAndCompanyIdAndStatus(String username, Long companyId, Status status,
			Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyIdAndStatus(username, companyId, status, pageable);
	}


	private Page<Offer> getOffersByUsernameAndCompanyIdAndStatusAndOnlyOverdue(String username, Long companyId,
			Status status, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyIdAndStatusAndValidUntilIsBefore(username, companyId, status, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffersByUsernameAndCompanyId(String username, Long companyId, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyId(username, companyId, pageable);
	}


	private Page<Offer> getOffersByUsernameAndCompanyIdAndOnlyOverdue(String username, Long companyId,
			Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyIdAndValidUntilIsBefore(username, companyId, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffersByUsernameAndStatus(String username, Status status, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndStatus(username, status, pageable);
	}


	private Page<Offer> getOffersByUsernameAndStatusAndOnlyOverdue(String username, Status status, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndStatusAndValidUntilIsBefore(username, status, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffersByUsername(String username, Pageable pageable)
	{
		return offerRepository.findByUserUsername(username, pageable);
	}


	private Page<Offer> getOffersByUsernameAndOnlyOverdue(String username, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndValidUntilIsBefore(username, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffersByCompanyIdAndStatus(Long companyId, Status status, Pageable pageable)
	{
		return offerRepository.findByCompanyIdAndStatus(companyId, status, pageable);
	}


	private Page<Offer> getOffersByCompanyIdAndStatusAndOnlyOverdue(Long companyId, Status status, Pageable pageable)
	{
		return offerRepository.findByCompanyIdAndStatusAndValidUntilIsBefore(companyId, status, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffersByCompanyId(Long companyId, Pageable pageable)
	{
		return offerRepository.findByCompanyId(companyId, pageable);
	}


	private Page<Offer> getOffersByCompanyIdAndOnlyOverdue(Long companyId, Pageable pageable)
	{
		return offerRepository.findByCompanyIdAndValidUntilIsBefore(companyId, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffersByStatus(Status status, Pageable pageable)
	{
		return offerRepository.findByStatus(status, pageable);
	}


	private Page<Offer> getOffersByStatusAndOnlyOverdue(Status status, Pageable pageable)
	{
		return offerRepository.findByStatusAndValidUntilIsBefore(status, LocalDate.now(), pageable);
	}


	private Page<Offer> getOffers(Pageable pageable)
	{
		return offerRepository.findAll(pageable);
	}


	private Page<Offer> getOffersByOnlyOverdue(Pageable pageable)
	{
		return offerRepository.findByValidUntilIsBefore(LocalDate.now(), pageable);
	}


	public Offer addOffer(Offer offer, String username)
	{

		User user = userService.getUser(username);

		if (user == null)
			throw new RuntimeException("User not specified!");

		if (offer.getNotes() == null)
			offer.setNotes("""
Plaćanje: po dogovoru
Rok realizacije: po dogovoru
Garantni period: 2 godine
""");

		offer.setOfferDate(LocalDate.now());

		offer.setId(null);

		offer.setStatus(Status.ACTIVE);

		offer.setUser(user);

		if (offer.getValidUntil() == null)
			offer.setValidUntil(LocalDate.now().plusDays(7));

		if (offer.getVat() == null)
			offer.setVat(BigDecimal.valueOf(17));

		return offerRepository.save(offer);

	}


	public Offer updateOffer(String offerId, Offer offer, String username)
	{

		User user = userService.getUser(username);

		if (user == null)
			throw new RuntimeException("User not specified!");

		Offer fromRep = getOffer(offerId);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		if (!user.equals(fromRep.getUser()))
		{
			throw new RuntimeException("Only user who created the offer is allowed to update it!");
		}

		fromRep.copyFieldsFrom(offer);

		return offerRepository.save(fromRep);

	}


	public void deleteOffer(String offerId)
	{

		Offer fromRep = getOffer(offerId);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		/*
		 * TODO: Provjeriti najprije šta je sa isporukama, narudžbama, pa tek onda brisati.
		 * Obrisati i zavisne objekte (Item)
		 */

		offerRepository.deleteById(offerId);

	}


	public Offer cancelOffer(String offerId, String reason)
	{

		Offer fromRep = getOffer(offerId);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		if (fromRep.getStatus().equals(Offer.Status.ACCEPTED))
			throw new RuntimeException("An offer which has been accepted can not be canceled!");

		if (fromRep.getStatus().equals(Offer.Status.CANCELED))
			throw new RuntimeException("The offer has already been canceled!");

		fromRep.setStatus(Status.CANCELED);
		if (reason != null)
		{
			if (fromRep.getComments() != null && !fromRep.getComments().trim().equals(""))
			{
				fromRep.setComments(fromRep.getComments() + "\n\n" + reason);
			} else {
				fromRep.setComments(reason);
			}
		}

		return offerRepository.save(fromRep);

	}


	public Offer acceptOffer(String offerId)
	{

		Offer fromRep = getOffer(offerId);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		if (fromRep.getStatus().equals(Status.CANCELED))
			throw new RuntimeException("An offer which has been canceled can not be accepted! Please create new offer by duplicating this one.");

		if (fromRep.getStatus().equals(Status.ACCEPTED))
			throw new RuntimeException("The offer has already been accepted!");

		orderService.orderItems(fromRep.getItems());

		fromRep.setStatus(Status.ACCEPTED);

		return offerRepository.save(fromRep);

	}


	public Offer duplicateOffer(String offerNo, String username)
	{

		User user = userService.getUser(username);

		if (user == null)
			throw new RuntimeException("User not specified!");

		Offer fromRep = getOffer(offerNo);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		Offer retVal = new Offer();
		retVal.copyFieldsFrom(fromRep);
		retVal.setOfferDate(LocalDate.now());
		retVal.setId(null);
		retVal.setStatus(Status.ACTIVE);
		retVal.setUser(user);
		retVal.setValidUntil(LocalDate.now().plusDays(7));

		Offer savedOffer = offerRepository.save(retVal);

		fromRep.getItems().forEach(i -> {
			Item item = new Item();
			item.copyFieldsFrom(i);
			item.setDelivery(null);
			item.setDeliveryNote(null);
			item.setOffer(savedOffer);
			item.setOrder(null);
			savedOffer.getItems().add(item);
		});

		itemService.updateItems(savedOffer.getItems());

		return savedOffer;

	}


	public List<Item> getItems(String offerId)
	{
		return itemService.getItemsByOfferNo(offerId);
	}


	public Item addItem(String offerId, Item i)
	{

		Offer offer = getOffer(offerId);

		if (offer == null)
			throw new RuntimeException("Offer not found in the database!");

		return itemService.addItem(offer, i);

	}


	public void deleteItem(String offerId, Long itemId)
	{
		itemService.deleteItem(offerId, itemId);
	}


	public Item updateItem(String offerId, Long itemId, Item i)
	{
		return itemService.updateItem(offerId, itemId, i);
	}


}
