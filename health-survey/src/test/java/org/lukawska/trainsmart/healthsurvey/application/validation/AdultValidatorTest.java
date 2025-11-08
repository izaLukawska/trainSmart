package org.lukawska.trainsmart.healthsurvey.application.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AdultValidatorTest {

    private final AdultValidator validator = new AdultValidator();

    @Test
    void shouldReturnTrueWhenBirthDateIsNull() {
        //when && then
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenExactly18YearsOld() {
        //given
        final LocalDate birthDate = LocalDate.now().minusYears(18);

        //when && then
        assertThat(validator.isValid(birthDate, null)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenYoungerThan18() {
        //given
        final LocalDate birthDate = LocalDate.now().minusYears(17);

        //when && then
        assertThat(validator.isValid(birthDate, null)).isFalse();
    }
}
