package org.lukawska.trainsmart.gymfinder.domain.util;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.gymfinder.domain.valueObject.GeoPoint;

@UtilityClass
public class GeoDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Computes great-circle distance using the Haversine formula.
     *
     * @param point1 First point latitude and latitude
     * @param point2 Second point latitude and longitude
     * @return distance in kilometers
     */
    public static double haversineDistanceKm(GeoPoint point1, GeoPoint point2) {
        double latitude1 = point1.latitude();
        double longitude1 = point1.longitude();

        double latitude2 = point2.latitude();
        double longitude2 = point2.longitude();

        double latitude1Radians = Math.toRadians(latitude1);
        double latitude2Radians = Math.toRadians(latitude2);
        double deltaLatitude = latitude2Radians - latitude1Radians;
        double deltaLongitudeRadians = Math.toRadians(longitude2 - longitude1);

        double haversineValue = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(latitude1Radians) * Math.cos(latitude2Radians)
                * Math.sin(deltaLongitudeRadians / 2) * Math.sin(deltaLongitudeRadians / 2);

        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(haversineValue), Math.sqrt(1 - haversineValue));
    }
}
