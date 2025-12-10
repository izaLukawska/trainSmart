package org.lukawska.trainsmart.trainingplan.application.preparation.resolvers;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@UtilityClass
class PreferredDaysResolver {

    static List<WeekDay> resolvePreferredDays(List<WeekDay> weekDays, int daysPerWeek) {
        return weekDays.isEmpty() ? getDefaultDays(daysPerWeek) : weekDays.stream()
                                                                          .sorted(Comparator.comparing(Enum::ordinal))
                                                                          .toList();
    }

    private List<WeekDay> getDefaultDays(int daysPerWeek) {
        return switch (daysPerWeek) {
            case 1 -> List.of(WeekDay.MONDAY);
            case 2 -> List.of(WeekDay.MONDAY, WeekDay.THURSDAY);
            case 3 -> List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY, WeekDay.FRIDAY);
            case 4 -> List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY);
            case 5 -> List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY);
            default -> Arrays.stream(WeekDay.values()).toList();
        };
    }
}
