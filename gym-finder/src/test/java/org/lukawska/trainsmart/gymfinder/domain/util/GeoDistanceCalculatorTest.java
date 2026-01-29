package org.lukawska.trainsmart.gymfinder.domain.util;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.gymfinder.domain.valueObject.GeoPoint;

import static org.assertj.core.api.Assertions.assertThat;

class GeoDistanceCalculatorTest {

    @Test
    void shouldCalculateAndReturnHaversineDistanceKm() {
        //given
        final GeoPoint geoPoint1 = new GeoPoint(52.2297, 21.0122);
        final GeoPoint geoPoint2 = new GeoPoint(50.0647, 19.9450);

        //when
        double distance = GeoDistanceCalculator.haversineDistanceKm(geoPoint1, geoPoint2);

        //then
        assertThat(distance).isCloseTo(252.00, Offset.offset(1.00));
    }
}
