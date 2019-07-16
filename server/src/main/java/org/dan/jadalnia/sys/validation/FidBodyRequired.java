package org.dan.jadalnia.sys.validation;

import org.dan.jadalnia.app.festival.Fid;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FidBodyRequired.Validator.class)
public @interface FidBodyRequired {
    String REQUEST_BODY_MUST_BE_A_FESTIVAL_ID = "request body must be a festival id";

    String message() default REQUEST_BODY_MUST_BE_A_FESTIVAL_ID;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<FidBodyRequired, Fid> {
        public void initialize(final FidBodyRequired hasId) {
        }

        public boolean isValid(final Fid fid, final ConstraintValidatorContext constraintValidatorContext) {
            return fid != null;
        }
    }
}
