package com.fruitshop.ecommerce.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveQuantityValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveQuantity {
    String message() default "Quantity must be greater than 0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 