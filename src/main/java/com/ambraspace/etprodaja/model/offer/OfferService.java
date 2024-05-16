package com.ambraspace.etprodaja.model.offer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambraspace.etprodaja.model.item.Item;
import com.ambraspace.etprodaja.model.offer.Offer.Status;
import com.ambraspace.etprodaja.model.user.User;
import com.ambraspace.etprodaja.model.user.UserService;

@Service
public class OfferService
{

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private UserService userService;


	public Offer getOffer(String offerId)
	{
		Offer retVal = offerRepository.findById(offerId).orElse(null); 
		if (retVal != null)
			fillTransientFields(List.of(retVal));
		return retVal;
	}


	@Transactional(readOnly = true)
	public Page<Offer> getOffers(String username, Long companyId, Status status, Long productId, Pageable pageable)
	{

		Page<String> offerIds;

		if (username == null)
		{
			if (companyId == null)
			{
				if (status == null)
				{
					if (productId != null)
					{
						offerIds = getOffersByProductId(productId, pageable);
					} else {
						offerIds = getOffers(pageable);
					}
				} else {
					if (productId != null)
					{
						offerIds = getOffersByStatusAndProductId(status, productId, pageable);
					} else {
						offerIds = getOffersByStatus(status, pageable);
					}
				}
			} else {
				if (status == null)
				{
					if (productId != null)
					{
						offerIds = getOffersByCompanyIdAndProductId(companyId, productId, pageable);
					} else {
						offerIds = getOffersByCompanyId(companyId, pageable);
					}
				} else {
					if (productId != null)
					{
						offerIds = getOffersByCompanyIdAndStatusAndProductId(companyId, status, productId, pageable);
					} else {
						offerIds = getOffersByCompanyIdAndStatus(companyId, status, pageable);
					}
				}
			}
		} else {
			if (companyId == null)
			{
				if (status == null)
				{
					if (productId != null)
					{
						offerIds = getOffersByUsernameAndProductId(username, productId, pageable);
					} else {
						offerIds = getOffersByUsername(username, pageable);
					}
				} else {
					if (productId != null)
					{
						offerIds = getOffersByUsernameAndStatusAndProductId(username, status, productId, pageable);
					} else {
						offerIds = getOffersByUsernameAndStatus(username, status, pageable);
					}
				}
			} else {
				if (status == null)
				{
					if (productId != null)
					{
						offerIds = getOffersByUsernameAndCompanyIdAndProductId(username, companyId, productId, pageable);
					} else {
						offerIds = getOffersByUsernameAndCompanyId(username, companyId, pageable);
					}
				} else {
					if (productId != null)
					{
						offerIds = getOffersByUsernameAndCompanyIdAndStatusAndProductId(username, companyId, status, productId, pageable);
					} else {
						offerIds = getOffersByUsernameAndCompanyIdAndStatus(username, companyId, status, pageable);
					}
				}
			}
		}

		List<Offer> selectedOffers = new ArrayList<Offer>();
		offerRepository.getOfferDetails(offerIds.getContent(), pageable.getSort()).forEach(selectedOffers::add);
		fillTransientFields(selectedOffers);
		return new PageImpl<Offer>(selectedOffers, pageable, offerIds.getTotalElements());

	}


	private Page<String> getOffersByUsernameAndCompanyIdAndStatus(String username, Long companyId, Status status,
			Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyIdAndStatus(username, companyId, status, pageable);
	}


	private Page<String> getOffersByUsernameAndCompanyIdAndStatusAndProductId(String username, Long companyId, Status status,
			Long productId, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyIdAndStatusAndProductId(username, companyId, status, productId, pageable);
	}


	private Page<String> getOffersByUsernameAndCompanyId(String username, Long companyId, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyId(username, companyId, pageable);
	}


	private Page<String> getOffersByUsernameAndCompanyIdAndProductId(String username, Long companyId, Long productId,
			Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndCompanyIdAndProductId(username, companyId, productId, pageable);
	}


	private Page<String> getOffersByUsernameAndStatus(String username, Status status, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndStatus(username, status, pageable);
	}


	private Page<String> getOffersByUsernameAndStatusAndProductId(String username, Status status, Long productId, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndStatusAndProductId(username, status, productId, pageable);
	}


	private Page<String> getOffersByUsername(String username, Pageable pageable)
	{
		return offerRepository.findByUserUsername(username, pageable);
	}


	private Page<String> getOffersByUsernameAndProductId(String username, Long productId, Pageable pageable)
	{
		return offerRepository.findByUserUsernameAndProductId(username, productId, pageable);
	}


	private Page<String> getOffersByCompanyIdAndStatus(Long companyId, Status status, Pageable pageable)
	{
		return offerRepository.findByCompanyIdAndStatus(companyId, status, pageable);
	}


	private Page<String> getOffersByCompanyIdAndStatusAndProductId(Long companyId, Status status, Long productId, Pageable pageable)
	{
		return offerRepository.findByCompanyIdAndStatusAndProductId(companyId, status, productId, pageable);
	}


	private Page<String> getOffersByCompanyId(Long companyId, Pageable pageable)
	{
		return offerRepository.findByCompanyId(companyId, pageable);
	}


	private Page<String> getOffersByCompanyIdAndProductId(Long companyId, Long productId, Pageable pageable)
	{
		return offerRepository.findByCompanyIdAndProductId(companyId, productId, pageable);
	}


	private Page<String> getOffersByStatus(Status status, Pageable pageable)
	{
		return offerRepository.findByStatus(status, pageable);
	}


	private Page<String> getOffersByStatusAndProductId(Status status, Long productId, Pageable pageable)
	{
		return offerRepository.findByStatusAndProductId(status, productId, pageable);
	}


	private Page<String> getOffers(Pageable pageable)
	{
		return offerRepository.getAllOffers(pageable);
	}


	private Page<String> getOffersByProductId(Long productId, Pageable pageable)
	{
		return offerRepository.findByProductId(productId, pageable);
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


	@Transactional
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


	@Transactional
	public void deleteOffer(String offerId)
	{

		Offer fromRep = getOffer(offerId);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		if (fromRep.getStatus().equals(Status.ACCEPTED))
			throw new RuntimeException("Offer which has been accepted cannot be deleted!");

		offerRepository.deleteById(offerId);

	}


	@Transactional
	Offer cancelOffer(String offerId, String reason)
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
			if (fromRep.getComments() != null && !fromRep.getComments().isBlank())
			{
				fromRep.setComments(fromRep.getComments() + "\n\n" + reason);
			} else {
				fromRep.setComments(reason);
			}
		}

		return offerRepository.save(fromRep);

	}


	@Transactional
	Offer acceptOffer(String offerId)
	{

		Offer fromRep = getOffer(offerId);

		if (fromRep == null)
			throw new RuntimeException("No such offer in the database!");

		if (fromRep.getStatus().equals(Status.CANCELED))
			throw new RuntimeException("An offer which has been canceled can not be accepted! Please create new offer by duplicating this one.");

		if (fromRep.getStatus().equals(Status.ACCEPTED))
			throw new RuntimeException("The offer has already been accepted!");

		fromRep.setStatus(Status.ACCEPTED);

		return offerRepository.save(fromRep);

	}


	public Offer duplicateOffer(Offer original, User user)
	{

		Offer retVal = new Offer();
		retVal.copyFieldsFrom(original);
		retVal.setOfferDate(LocalDate.now());
		retVal.setId(null);
		retVal.setStatus(Status.ACTIVE);
		retVal.setUser(user);
		retVal.setValidUntil(LocalDate.now().plusDays(7));

		return offerRepository.save(retVal);

	}


	@Transactional
	public void deleteAllOffers()
	{

		offerRepository.deleteAll();

	}


	private void fillTransientFields(List<Offer> offers)
	{

		for (Offer o:offers)
		{

			BigDecimal value = BigDecimal.ZERO;

			BigDecimal cost = BigDecimal.ZERO;

			if (o.getItems() != null && o.getItems().size() > 0)
			{

				for (Item i:o.getItems())
				{

					value = value.add(
						i.getNetPrice()
						.multiply(
								i.getQuantity()
						)
						.setScale(2, RoundingMode.HALF_EVEN)
					);

					cost = cost.add(
							i.getStockInfo().getUnitPrice()
							.multiply(
									i.getQuantity()
							)
							.setScale(2, RoundingMode.HALF_EVEN)
						);

				}

			}

			o.setValue(value);

			o.setCost(cost);

			o.setMargin(
					cost.compareTo(BigDecimal.ZERO) == 0 ?
							BigDecimal.ZERO :
								value
								.divide(cost, 4, RoundingMode.HALF_EVEN)
								.subtract(BigDecimal.ONE)
								.movePointRight(2)
			);

		}

	}

}
