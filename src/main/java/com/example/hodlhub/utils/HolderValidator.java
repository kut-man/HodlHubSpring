package com.example.hodlhub.utils;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.repositories.HolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class HolderValidator implements Validator {

    private final HolderRepository holderRepository;

    @Autowired
    public HolderValidator(HolderRepository holderRepository) {
        this.holderRepository = holderRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Holder.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Holder holder = (Holder) target;

        if (holderRepository.findByEmail(holder.getEmail()).isEmpty()){
            errors.rejectValue("email", "409", "Invalid Email");
        }
    }
}
