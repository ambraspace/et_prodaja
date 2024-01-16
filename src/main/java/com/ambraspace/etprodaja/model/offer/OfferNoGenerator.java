package com.ambraspace.etprodaja.model.offer;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

public class OfferNoGenerator implements IdentifierGenerator
{

	private static final long serialVersionUID = 1L;

	private final OfferNoSequence sequence;

	public OfferNoGenerator	(OfferNoSequence config,
			Member annotatedMember,
			CustomIdGeneratorCreationContext context) {
		this.sequence = config;
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj)
	{

		String prefix = String.format("P-%d-", LocalDate.now().getYear());

		String query = String.format("select %1$s from %2$s where %1$s like '%3$s%%'",
				session.getEntityPersister(obj.getClass().getName(), obj)
				.getIdentifierPropertyName(),
				obj.getClass().getSimpleName(), prefix);

		Stream<String> ids = session.createSelectionQuery(query, String.class).stream();

		Long max = ids.map(o -> o.replace(prefix, ""))
				.mapToLong(Long::parseLong)
				.max()
				.orElse(0L);

		if (max == 0L)
			max += sequence.startWith();
		else
			max += sequence.incrementBy();

		return prefix + max;

	}

}


