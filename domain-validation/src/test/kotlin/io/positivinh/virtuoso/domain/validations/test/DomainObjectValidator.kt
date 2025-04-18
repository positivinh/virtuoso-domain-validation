package io.positivinh.virtuoso.domain.validations.test

import com.crabshue.commons.kotlin.logging.getLogger
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import org.assertj.core.api.Assertions
import kotlin.reflect.KClass

object DomainObjectValidator {

    private val log = getLogger()

    fun <T> validateValidObject(objectToValidate: T): Unit {

        val constraintViolations = this.validate(objectToValidate)

        Assertions.assertThat(constraintViolations).isEmpty()
    }

    fun <T> validate(objectToValidate: T): Set<ConstraintViolation<T>> {

        val validatorFactory = Validation.buildDefaultValidatorFactory()
        val validator = validatorFactory.validator

        val ret: Set<ConstraintViolation<T>> = validator.validate(objectToValidate)

        log.info("Validated [{}]. Constraints violations discovered: [{}]", objectToValidate, ret)

        return ret
    }

    fun <T> assertConstraintViolation(objectToValidate: T, fieldInError: String, annotation: KClass<*>) {

        val constraintViolations = validate(objectToValidate)

        Assertions.assertThat(constraintViolations).isNotEmpty()

        val notBlankConstraintValidation = constraintViolations
            .filter { it.propertyPath.toString() == fieldInError }
            .filter { it.constraintDescriptor.annotation.annotationClass == annotation }

        Assertions.assertThat(notBlankConstraintValidation).isNotNull
    }

    fun setErrorMessage(constraintValidatorContext: ConstraintValidatorContext, message: String?) {

        constraintValidatorContext.disableDefaultConstraintViolation()

        constraintValidatorContext
            .buildConstraintViolationWithTemplate(message)
            .addConstraintViolation()
    }
}
