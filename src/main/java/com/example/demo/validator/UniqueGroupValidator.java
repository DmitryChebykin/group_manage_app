package com.example.demo.validator;

import com.example.demo.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@AllArgsConstructor
public class UniqueGroupValidator implements ConstraintValidator<UniqueGroup, String> {
    private final GroupRepository groupRepository;

    @Override
    public void initialize(UniqueGroup constraintAnnotation) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        if (groupRepository.existsByGroupNumber(s)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Группа с таким номером уже есть")
                    .addPropertyNode("group")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

