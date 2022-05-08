package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
public class Location {
    public static final double METERS_PER_DEGREE = 111_000;
    public static final double STANDARD_DRIVING_SPEED = 70; //In kilometers per hour
    
    @Getter
    private double latitude;
    @Getter
    private double longitude;
    
    
    public Location() {
    }
    
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public long getDistanceTo(Location otherLocation) {
        return Math.round((distance(latitude, longitude, otherLocation.getLatitude(), otherLocation.getLongitude())) * 1000);
    }
    
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return (dist);
        }
    }
    
    /**
     * @return time in minutes
     */
    public long getTravelTimeInMinutes(Location otherLocation) {
        return (long) (getDistanceTo(otherLocation) / 1000d / STANDARD_DRIVING_SPEED * 60d);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
