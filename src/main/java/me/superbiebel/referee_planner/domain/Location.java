package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

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
        double latitudeDiff = otherLocation.latitude - this.latitude;
        double longitudeDiff = otherLocation.longitude - this.longitude;
        return (long) ceil(sqrt(latitudeDiff * latitudeDiff + longitudeDiff * longitudeDiff) * METERS_PER_DEGREE);
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
