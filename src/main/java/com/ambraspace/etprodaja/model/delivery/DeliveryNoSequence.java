package com.ambraspace.etprodaja.model.delivery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.id.enhanced.Optimizer;

@IdGeneratorType(DeliveryNoGenerator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DeliveryNoSequence
{
	String name();

	int startWith() default 1;

	int incrementBy() default 1;

	Class<? extends Optimizer> optimizer() default Optimizer.class;
}
