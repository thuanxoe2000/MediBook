package com.example.MediBook.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch,Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            var first = value.getClass().getDeclaredField(firstFieldName);
            var second = value.getClass().getDeclaredField(secondFieldName);

            first.setAccessible(true);
            second.setAccessible(true);

            Object firstObj = first.get(value);
            Object secondObj = second.get(value);

            boolean valid = Objects.equals(firstObj, secondObj);

            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(secondFieldName)
                        .addConstraintViolation();
            }

            return valid;
        } catch (Exception e) {
            return false;
        }
    }
}
