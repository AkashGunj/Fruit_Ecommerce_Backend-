package com.fruitshop.ecommerce.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositiveQuantityValidator implements ConstraintValidator<PositiveQuantity, Integer> {

    @Override
    public void initialize(PositiveQuantity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value > 0;
    }
} 