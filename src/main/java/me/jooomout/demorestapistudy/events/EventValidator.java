package me.jooomout.demorestapistudy.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, BindingResult errors){
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            // rejectValue - 필드에 들어가는 에러
            //errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            // reject - 글로벌 에러
            errors.addError(new ObjectError("event", "wrong value")); //글로벌 에러
            // 필드 에러
            errors.addError(new FieldError("event", "price", "value for price are wrong"));
            errors.reject("wrongPrices", "Values for prices are wrong");
        }

        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())
        ) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong");
        }
    }
}
