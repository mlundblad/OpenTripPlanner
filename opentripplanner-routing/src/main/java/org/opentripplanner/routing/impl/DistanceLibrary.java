/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.routing.impl;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class DistanceLibrary {

    public static final double RADIUS_OF_EARTH_IN_KM = 6371.01;

    public static final double distance(Coordinate from, Coordinate to) {
        return distance(from.y, from.x, to.y, to.x);
    }

    public static final double distance(double lat1, double lon1, double lat2, double lon2) {
        return distance(lat1, lon1, lat2, lon2, RADIUS_OF_EARTH_IN_KM * 1000);
    }

    public static final double distance(double lat1, double lon1, double lat2, double lon2,
            double radius) {

        // http://en.wikipedia.org/wiki/Great-circle_distance
        lat1 = toRadians(lat1); // Theta-s
        lon1 = toRadians(lon1); // Lambda-s
        lat2 = toRadians(lat2); // Theta-f
        lon2 = toRadians(lon2); // Lambda-f

        double deltaLon = lon2 - lon1;

        double y = sqrt(DistanceLibrary.p2(cos(lat2) * sin(deltaLon))
                + DistanceLibrary.p2(cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)));
        double x = sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(deltaLon);

        return radius * atan2(y, x);
    }

    private static final double p2(double a) {
        return a * a;
    }

    /** this is an overestimate */
    public static double metersToDegrees(double distance) {
        return 360 * distance / (2 * Math.PI * RADIUS_OF_EARTH_IN_KM * 1000);
    }

    public static final Envelope bounds(double lat, double lon, double latDistance,
            double lonDistance) {

        double radiusOfEarth = RADIUS_OF_EARTH_IN_KM * 1000;

        double latRadians = toRadians(lat);
        double lonRadians = toRadians(lon);

        double latRadius = radiusOfEarth;
        double lonRadius = Math.cos(latRadians) * radiusOfEarth;

        double latOffset = latDistance / latRadius;
        double lonOffset = lonDistance / lonRadius;

        double latFrom = toDegrees(latRadians - latOffset);
        double latTo = toDegrees(latRadians + latOffset);

        double lonFrom = toDegrees(lonRadians - lonOffset);
        double lonTo = toDegrees(lonRadians + lonOffset);

        return new Envelope(new Coordinate(lonFrom, latFrom), new Coordinate(lonTo, latTo));
    }
}
