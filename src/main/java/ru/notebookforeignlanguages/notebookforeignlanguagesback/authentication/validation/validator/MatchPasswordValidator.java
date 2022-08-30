package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.validator;

import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.PasswordResetRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.annotation.MatchPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchPasswordValidator implements ConstraintValidator<MatchPassword, PasswordResetRequest> {

    private Boolean allowNull;

    @Override
    public void initialize(MatchPassword constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(
            PasswordResetRequest value,
            ConstraintValidatorContext context
    ) {
        String password = value.getPassword();
        String confirmPassword = value.getConfirmPassword();
        if (allowNull) {
            return null == password && null == confirmPassword;
        }
        return password.equals(confirmPassword);
    }
}
