package com.ambraspace.etprodaja.model.order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.id.enhanced.Optimizer;

@IdGeneratorType(OrderNoGenerator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderNoSequence
{
	String name();

	int startWith() default 1;

	int incrementBy() default 1;

	Class<? extends Optimizer> optimizer() default Optimizer.class;
}
