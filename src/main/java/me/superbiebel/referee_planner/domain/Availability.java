package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
public class Availability {
    @Getter
    private Location startLocation;
    
    @Getter
    private boolean endLocationEnabled;
    @Getter
    private Location endLocation;
    
    @Getter
    private TimePeriod timePeriod;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Availability that = (Availability) o;
        
        if (isEndLocationEnabled() != that.isEndLocationEnabled()) return false;
        if (!getStartLocation().equals(that.getStartLocation())) return false;
        if (!getEndLocation().equals(that.getEndLocation())) return false;
        return getTimePeriod().equals(that.getTimePeriod());
    }
    
    @Override
    public int hashCode() {
        int result = getStartLocation().hashCode();
        result = 31 * result + (isEndLocationEnabled() ? 1 : 0);
        result = 31 * result + getEndLocation().hashCode();
        result = 31 * result + getTimePeriod().hashCode();
        return result;
    }
}
