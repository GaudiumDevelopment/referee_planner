package me.superbiebel.referee_planner.domain;

import lombok.Getter;

public class Location {
    @Getter
    public final double latitude;
    @Getter
    public final double longitude;
    
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
