package com.upgrade.bookingservice.validator;

import com.upgrade.bookingservice.controller.dto.AvailabilityRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class AvailabilityValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return AvailabilityRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AvailabilityRequest availability = (AvailabilityRequest) obj;

        if(availability.getFrom().isEqual(availability.getTo())) {
            errors.reject("Start date should be different from end date");
        }
        if(availability.getFrom().isBefore(LocalDate.now())) {
            errors.reject("Start date should be after today");
        }
        if(availability.getTo().isBefore(LocalDate.now())) {
            errors.reject("End date should be after today");
        }
        if(availability.getFrom().isAfter(availability.getTo())) {
            errors.reject("Start date should be before end date");
        }
    }

}
